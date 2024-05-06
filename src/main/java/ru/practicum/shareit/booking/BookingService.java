package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto addBooking(BookingDto bookingDto);

    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);

    BookingDto getBooking(Long bookingId);

    void deleteBooking(Long bookingId);

    void approveBooking(Long userId, Long bookingId);

    void rejectBooking(Long userId, Long bookingId);

    void cancelBooking(Long userId, Long bookingId);
}
