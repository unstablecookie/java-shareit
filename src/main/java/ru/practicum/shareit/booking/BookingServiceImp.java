package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.TimeOverlapException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        userRepository.getUser(bookingDto.getBooker());
        itemRepository.getItem(bookingDto.getItem());
        Item item = itemRepository.getItem(bookingDto.getItem());
        User booker = userRepository.getUser(bookingDto.getBooker());
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        checkTimeOverlap(booking);
        return BookingMapper.toBookingDto(bookingRepository.addBooking(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, BookingDto bookingDto) {
        Booking oldBooking = bookingRepository.getBooking(bookingId);
        if (oldBooking == null) {
            throw new EntityNotFoundException(String.format("booking id %d not found", bookingId)
            );
        }
        Item item = itemRepository.getItem(bookingDto.getItem());
        User booker = userRepository.getUser(bookingDto.getBooker());
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        Booking updatedBooking = BookingMapper.updateBookingWithBooking(oldBooking, booking);
        checkTimeOverlap(updatedBooking);
        bookingRepository.updateBooking(bookingId, booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if (booking == null) {
            throw new EntityNotFoundException(String.format("booking id %d not found", bookingId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if (booking == null) {
            throw new EntityNotFoundException(String.format("booking id %d not found", bookingId));
        }
        bookingRepository.deleteBooking(booking);
    }

    @Override
    public void approveBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (!booking.getItem().getOwner().equals(userId))) {
            throw new EntityNotFoundException(String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId));
        }
        booking.setStatus(Status.APPROVED);
        bookingRepository.updateBooking(bookingId, booking);
    }

    @Override
    public void rejectBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (!booking.getItem().getOwner().equals(userId))) {
            throw new EntityNotFoundException(String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId)
            );
        }
        booking.setStatus(Status.REJECTED);
        bookingRepository.updateBooking(bookingId, booking);
    }

    @Override
    public void cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (!booking.getBooker().getId().equals(userId))) {
            throw new EntityNotFoundException(String.format("user id %d is not owner or booking does not exist",
                    userId, bookingId)
            );
        }
        booking.setStatus(Status.CANCELED);
        bookingRepository.updateBooking(bookingId, booking);
    }

    private void checkTimeOverlap(Booking booking) {
        Set<Booking> itemBookings = bookingRepository.getItemBookings(booking.getItem().getId());
        itemBookings.stream()
                .filter(x -> x.getItem().getId().equals(booking.getItem().getId()))
                .forEach(x -> isItOverlapping(x.getStart(), x.getEnd(), booking.getStart(), booking.getEnd()));
    }

    private void isItOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        long overlap1 = Math.min(end1.toEpochDay() - start1.toEpochDay(),
                end1.toEpochDay() - start2.toEpochDay());
        long overlap2 = Math.min(end2.toEpochDay() - start2.toEpochDay(),
                end2.toEpochDay() - start1.toEpochDay());
        if (Math.min(overlap1, overlap2) >= 0) {
            throw new TimeOverlapException("Booking for this period already exists");
        }
    }
}
