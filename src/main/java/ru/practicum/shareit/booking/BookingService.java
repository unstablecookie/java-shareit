package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.State;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<BookingDtoFull> addBooking(BookingDto bookingDto, Long userId);

    Optional<BookingDtoFull> getBooking(Long bookingId, Long userId);

    Optional<List<BookingDtoFull>> getUserBookings(Long userId);

    Optional<List<BookingDtoFull>> getOwnerBookings(Long userId);

    Optional<List<BookingDtoFull>> getOwnerBookingsWithState(Long userId,  State queryStatus);

    Optional<List<BookingDtoFull>> getUserBookingsWithState(Long userId,  State queryStatus);

    void deleteBooking(Long bookingId);

    Optional<BookingDtoFull> updateBooking(Long userId, Long bookingId, Boolean approved);

    void cancelBooking(Long userId, Long bookingId);
}
