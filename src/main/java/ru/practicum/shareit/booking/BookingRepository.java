package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Set;

public interface BookingRepository {
    Booking addBooking(Booking booking);

    void updateBooking(Long bookingId, Booking booking);

    Booking getBooking(Long bookingId);

    Set<Booking> getItemBookings(Long itemId);

    void deleteBooking(Booking booking);

    void deleteItemBookings(Long itemId);
}
