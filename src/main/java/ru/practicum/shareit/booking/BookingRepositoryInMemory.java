package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.error.TimeOverlapException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class BookingRepositoryInMemory implements BookingRepository {
    private final Map<Long, Booking> storage = new HashMap<>();
    private final Map<Long, Set<Booking>> timeTable = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Booking addBooking(Booking booking) {
        checkTimeOverlap(booking);
        booking.setId(counter);
        booking.setStatus(Status.WAITING);
        counter++;
        storage.put(booking.getId(), booking);
        Set<Booking> itemBookings = timeTable.get(booking.getItem().getId());
        if (itemBookings == null) {
            timeTable.put(booking.getItem().getId(), new HashSet<>());
            itemBookings = timeTable.get(booking.getItem().getId());
        }
        itemBookings.add(booking);
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookingId, Booking booking) {
        Booking oldBooking = storage.get(bookingId);
        if (oldBooking == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("booking id %d not found", bookingId)
            );
        }
        Booking updatedBooking = BookingMapper.updateBookingWithBooking(oldBooking, booking);
        checkTimeOverlap(updatedBooking);
        return updatedBooking;
    }

    @Override
    public Booking getBooking(Long bookingId) {
        Booking booking = storage.get(bookingId);
        if (booking == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("booking id %d not found", bookingId)
            );
        }
        return booking;
    }

    @Override
    public void deleteBooking(Long bookingId) {
        Booking booking = storage.get(bookingId);
        if (booking != null) {
            Item item = booking.getItem();
            Set<Booking> bookings = timeTable.get(item.getId());
            bookings.remove(bookingId);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("booking id %d not found", bookingId)
            );
        }
        storage.remove(bookingId);
    }

    @Override
    public void deleteItemBookings(Long itemId) {
        Set<Booking> bookings = timeTable.get(itemId);
        if (bookings == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("item id %d do not exist", itemId)
            );
        }
        bookings.stream().forEach(x -> storage.remove(x.getId()));
        timeTable.remove(itemId);
    }

    private void checkTimeOverlap(Booking booking) {
        Set<Booking> itemBookings = timeTable.get(booking.getItem().getId());
        if (itemBookings == null) {
            return;
        }
        for (Booking b :  itemBookings) {
            if (booking.getId() == b.getId()) {
                continue;
            }
            if (isItOverlapping(b.getStart(), b.getEnd(), booking.getStart(), booking.getEnd())) {
                throw new TimeOverlapException("Booking for this period already exists");
            }
        }
    }

    private boolean isItOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        long overlap1 = Math.min(end1.toEpochDay() - start1.toEpochDay(),
                end1.toEpochDay() - start2.toEpochDay());
        long overlap2 = Math.min(end2.toEpochDay() - start2.toEpochDay(),
                end2.toEpochDay() - start1.toEpochDay());
        return Math.min(overlap1, overlap2) >= 0;
    }
}
