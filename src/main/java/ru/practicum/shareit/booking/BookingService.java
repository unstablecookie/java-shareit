package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingFullDto addBooking(BookingDto bookingDto, Long userId);

    BookingFullDto getBooking(Long bookingId, Long userId);

    List<BookingFullDto> getUserBookings(Long userId);

    List<BookingFullDto> getOwnerBookings(Long userId);

    List<BookingFullDto> getOwnerBookingsWithState(Long userId, State queryStatus);

    List<BookingFullDto> getUserBookingsWithState(Long userId, State queryStatus);

    void deleteBooking(Long bookingId);

    BookingFullDto updateBooking(Long userId, Long bookingId, Boolean approved);

    void cancelBooking(Long userId, Long bookingId);
}
