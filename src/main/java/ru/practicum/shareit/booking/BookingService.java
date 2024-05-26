package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingFullDto addBooking(BookingDto bookingDto, Long userId);

    BookingFullDto getBooking(Long bookingId, Long userId);

    List<BookingFullDto> getUserBookings(Long userId, int from, int size);

    List<BookingFullDto> getOwnerBookings(Long userId, int from, int size);

    List<BookingFullDto> getOwnerBookingsWithState(Long userId, State queryStatus, int from, int size);

    List<BookingFullDto> getUserBookingsWithState(Long userId, State queryStatus, int from, int size);

    void deleteBooking(Long bookingId);

    BookingFullDto updateBooking(Long userId, Long bookingId, Boolean approved);

    void cancelBooking(Long userId, Long bookingId);
}
