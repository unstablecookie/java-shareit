package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository {
    Booking addBooking(Booking booking);

    Booking updateBooking(Long bookingId, Booking booking);

    Booking getBooking(Long bookingId);

    void deleteBooking(Long bookingId);

    void deleteItemBookings(Long itemId);
}
