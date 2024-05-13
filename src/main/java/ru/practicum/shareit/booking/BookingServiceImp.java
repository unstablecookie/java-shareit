package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.EntityNotFoundException;
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
    public Optional<BookingDtoFull> addBooking(BookingDto bookingDto, Long userId) {
        bookingDto.setBookerId(userId);
        Optional<User> booker = userRepository.findById(bookingDto.getBookerId());
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong booking attributes");
        }
        if (booker.isEmpty()) {
            throw new EntityNotFoundException(String.format("user id %d not found", bookingDto.getBookerId()));
        }
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty() || item.get().getOwner().equals(userId)) {
            return Optional.empty();
        }
        if (!item.get().getAvailable().booleanValue()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item is unavailable");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item.get(), booker.get());
        if (checkTimeOverlap(booking)) {
            return Optional.empty();
        }
        booking.setStatus(Status.WAITING);
        return Optional.of(BookingMapper.toBookingDtoFull(bookingRepository.save(booking)));
    }

    @Override
    public Optional<BookingDtoFull> getBooking(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            return Optional.empty();
        }
        if (!booking.get().getUser().getId().equals(userId) && !booking.get().getItem().getOwner().equals(userId)) {
            return Optional.empty();
        }
        return Optional.of(BookingMapper.toBookingDtoFull(booking.get()));
    }

    @Override
    public Optional<List<BookingDtoFull>> getOwnerBookings(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Item> items = itemRepository.findByOwner(userId);
        Set<Booking> bookings = items.stream()
                .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                .flatMap(x -> x.stream())
                .collect(Collectors.toSet());
        return Optional.of(bookings.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))//TODO
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BookingDtoFull>> getOwnerBookingsWithState(Long userId, State queryStatus) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Item> items = itemRepository.findByOwner(userId);
        List<Booking> bookings;
        if (queryStatus.equals(State.REJECTED) || queryStatus.equals(State.WAITING)) {
            Status status = Status.valueOf(queryStatus.name());
            bookings = items.stream()
                    .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                    .flatMap(x -> x.stream())
                    .filter(x -> x.getStatus().equals(status))
                    .collect(Collectors.toList());
        } else {
            switch (queryStatus) {
                case ALL: bookings = items.stream()
                        .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                        .flatMap(x -> x.stream())
                        .collect(Collectors.toList());
                    break;
                case PAST: bookings = items.stream()
                        .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                        .flatMap(x -> x.stream())
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                    break;
                case FUTURE: bookings = items.stream()
                        .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                        .flatMap(x -> x.stream())
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                    break;
                case CURRENT: bookings = items.stream()
                        .map(x -> bookingRepository.findByItemIdOrderByStartDesc(x.getId()))
                        .flatMap(x -> x.stream())
                        .filter(x -> x.getStart().isBefore(LocalDateTime.now()) &&
                                x.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                    break;
                default: return Optional.empty();
            }
        }
        return Optional.of(bookings.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BookingDtoFull>> getUserBookingsWithState(Long userId, State queryStatus) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Booking> bookings;
        if (queryStatus.equals(State.REJECTED) || queryStatus.equals(State.WAITING)) {
            Status status = Status.valueOf(queryStatus.name());
            bookings = bookingRepository.findUsersBookings(userId).stream()
                    .filter(x -> x.getStatus().equals(status))
                    .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                    .collect(Collectors.toList());
        } else {
            switch (queryStatus) {
                case ALL: bookings = bookingRepository.findUsersBookings(userId).stream()
                        .collect(Collectors.toList());
                break;
                case PAST: bookings = bookingRepository.findUsersBookings(userId).stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
                case FUTURE: bookings = bookingRepository.findUsersBookings(userId).stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
                case CURRENT: bookings = bookingRepository.findUsersBookings(userId).stream()
                        .filter(x -> x.getStart().isBefore(LocalDateTime.now()) &&
                                x.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
                default: return Optional.empty();
            }
        }
        return Optional.of(bookings.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BookingDtoFull>> getUserBookings(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        List<Booking> bookings = bookingRepository.findUsersBookings(userId);
        return Optional.of(bookings.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .sorted((a, b) -> b.getStart().compareTo(a.getStart()))
                .collect(Collectors.toList()));
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
    public Optional<BookingDtoFull> updateBooking(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (!booking.get().getItem().getOwner().equals(userId)) {
            throw new EntityNotFoundException("user is not an owner");
        }
        if (booking.isEmpty()) {
            return Optional.empty();
        }
        if (approved.booleanValue()) {
            if (booking.get().getStatus().equals(Status.APPROVED)) {
                return Optional.empty();
            }
            booking.get().setStatus(Status.APPROVED);
        } else {
            booking.get().setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking.get());
        return Optional.of(BookingMapper.toBookingDtoFull(booking.get()));
    }

    @Override
    public void cancelBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty() || (!booking.get().getUser().getId().equals(userId))) {
            throw new EntityNotFoundException(String.format("user id %d is not owner or booking does not exist",
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
}
