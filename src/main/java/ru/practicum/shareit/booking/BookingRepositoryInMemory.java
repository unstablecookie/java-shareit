package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryInMemory implements BookingRepository {
    private final Map<Long, Booking> storage = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Booking addBooking(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(counter);
            counter++;
        }
        booking.setStatus(Status.WAITING);
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void updateBooking(Long bookingId, Booking booking) {
        storage.put(bookingId, booking);
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return storage.get(bookingId);
    }

    @Override
    public Set<Booking> getItemBookings(Long itemId) {
        return storage.values().stream()
                .filter(x -> x.getItem().equals(itemId))
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteBooking(Booking booking) {
        storage.remove(booking.getId());
    }

    @Override
    public void deleteItemBookings(Long itemId) {
        storage.values().stream()
                .filter(x -> x.getItem().getId().equals(itemId))
                .forEach(x -> storage.remove(x.getId()));
    }
}
