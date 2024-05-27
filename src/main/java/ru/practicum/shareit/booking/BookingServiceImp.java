package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.error.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingFullDto addBooking(BookingDto bookingDto, Long userId) {
        bookingDto.setBookerId(userId);
        User booker = userRepository.findById(bookingDto.getBookerId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong booking attributes");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("item id: %d was not found", bookingDto.getItemId())));
        if (item.getOwner().equals(userId)) {
            throw new UserMissMatchException("user is the owner");
        }
        if (!item.getAvailable().booleanValue()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item is unavailable");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        if (checkTimeOverlap(booking)) {
            throw new TimeOverlapException("wrong bookings time period");
        }
        BookingFullDto addedBooking =
                Optional.of(BookingMapper.toBookingDtoFull(bookingRepository.save(booking)))
                        .orElseThrow(() -> new EntityNotFoundException("booking was not added"));
        return addedBooking;
    }

    @Override
    public BookingFullDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("booking was not found"));
        if (!booking.getUser().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new EntityNotFoundException("booking was not found");
        }
        return BookingMapper.toBookingDtoFull(booking);
    }

    @Override
    public List<BookingFullDto> getOwnerBookings(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return bookingRepository.findAllOwnerBookingsOrderByStartDesc(userId, page).stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingFullDto> getOwnerBookingsWithState(Long userId, State queryStatus, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (queryStatus) {
            case WAITING: return getBookingsWithConditionAndState(userId, Status.WAITING,
                    bookingRepository::findAllOwnerBookingsAndStatus, page);
            case REJECTED: return getBookingsWithConditionAndState(userId, Status.REJECTED,
                    bookingRepository::findAllOwnerBookingsAndStatus, page);
            case ALL: return getBookingsWithCondition(userId, bookingRepository::findAllOwnerBookings, page);
            case PAST: return getBookingsWithTimeCon(userId, bookingRepository::findByOwnerIdAndEndBefore, page);
            case FUTURE: return getBookingsWithTimeCon(userId, bookingRepository::findByOwnerIdAndStartAfter, page);
            case CURRENT: return getBookingsWithTimeCon(userId, bookingRepository::findByOwnerIdAndTimeCurrent, page);
            default: return getBookingsWithCondition(userId, bookingRepository::findAllOwnerBookings, page);
        }
    }

    @Override
    public List<BookingFullDto> getUserBookingsWithState(Long userId, State queryStatus, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        List<Booking> bookings;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (queryStatus) {
            case WAITING: return getBookingsWithConditionAndState(userId, Status.WAITING,
                    bookingRepository::findByUserIdAndStatus, page);
            case REJECTED: return getBookingsWithConditionAndState(userId, Status.REJECTED,
                    bookingRepository::findByUserIdAndStatus, page);
            case ALL: return getBookingsWithCondition(userId, bookingRepository::findByUserId, page);
            case PAST: return getBookingsWithTimeCon(userId, bookingRepository::findByUserIdAndEndBefore, page);
            case FUTURE: return getBookingsWithTimeCon(userId, bookingRepository::findByUserIdAndStartAfter, page);
            case CURRENT: return getBookingsWithTimeCon(userId, bookingRepository::findByUserIdAndTimeCurrent, page);
            default: return getBookingsWithCondition(userId, bookingRepository::findByUserId, page);
        }
    }

    @Override
    public List<BookingFullDto> getUserBookings(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return bookingRepository.findByUserIdOrderByStartDesc(userId, page).stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new EntityNotFoundException(String.format("booking id %d not found", bookingId));
        }
        bookingRepository.delete(booking.get());
    }

    @Override
    public BookingFullDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.format("booking id: %d was not found", bookingId)));
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new UserMissMatchException(String.format("user id: %d is not owner", userId));
        }
        if (approved.booleanValue()) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new UnsupportedStatusException();
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoFull(booking);
    }

    @Override
    public void cancelBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty() || (!booking.get().getUser().getId().equals(userId))) {
            throw new EntityNotFoundException(String.format("user id %d is not an owner or booking id: %d does not exist",
                    userId, bookingId)
            );
        }
        booking.get().setStatus(Status.CANCELED);
        bookingRepository.save(booking.get());
    }

    private boolean checkTimeOverlap(Booking booking) {
        Set<Booking> itemBookings = bookingRepository.findByItemIdOrderByStartDesc(booking.getItem().getId())
                .stream().collect(Collectors.toSet());
        Set<Booking> overlapedBookings = itemBookings.stream()
                .filter(x -> isItOverlapping(x.getStart(), x.getEnd(), booking.getStart(), booking.getEnd()))
                .limit(1)
                .collect(Collectors.toSet());
        return overlapedBookings.size() > 0;
    }

    private boolean isItOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if (start1.isBefore(start2) && end1.isAfter(start2)) {
            return true;
        }
        if (start2.isBefore(start1) && end2.isAfter(start1)) {
            return true;
        }
        if (start1.isBefore(start2) && end1.isAfter(end2)) {
            return true;
        }
        if (start2.isBefore(start1) && end2.isAfter(end1)) {
            return true;
        }
        if (start1.isEqual(start2) && end1.isEqual(end2)) {
            return true;
        }
        return false;
    }

    private List<BookingFullDto> getBookingsWithCondition(Long userId, EntityBookings entityMethod, Pageable page) {
        return entityMethod.getEntityBookingsOrderByStartDesc(userId, page).stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList());
    }

    private List<BookingFullDto> getBookingsWithTimeCon(Long userId, EntityBookingsWithOneTimeCond entityMethod, Pageable page) {
        return entityMethod.getEntityBookingsWithTimeCon(userId, LocalDateTime.now(), page).stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList());
    }

    private List<BookingFullDto> getBookingsWithConditionAndState(Long userId, Status status,
                                                                  EntityBookingsWithState entityMethod, Pageable page) {
        return entityMethod.getEntityBookingsWithState(userId, status, page).stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList());
    }
}
