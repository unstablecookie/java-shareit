package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFull;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.UnsupportedStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoFull addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {
        log.info("add new booking");
        Optional<BookingDtoFull> addedBookingFull = bookingService.addBooking(bookingDto, userId);
        if (addedBookingFull.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }
        return addedBookingFull.get();
    }

    @GetMapping("/{bookingId}")
    public BookingDtoFull getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info(String.format("get booking id: %d", bookingId));
        Optional<BookingDtoFull> bookingDtoFull = bookingService.getBooking(bookingId, userId);
        if (bookingDtoFull.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }
        return bookingDtoFull.get();
    }

    @GetMapping("/owner")
    public List<BookingDtoFull> getBookingWithOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("get owner id: %d bookings", userId));
        Optional<List<BookingDtoFull>> bookings = bookingService.getOwnerBookings(userId);
        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }
        return bookings.get();
    }

    @GetMapping(value = "/owner", params = "state")
    public List<BookingDtoFull> getBookingWithOwnerWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(required = true, defaultValue = "ALL") String state) {
        log.info(String.format("get owner id: %d bookings with state", userId));
        State queryStatus;
        try {
            queryStatus = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }
        Optional<List<BookingDtoFull>> bookings = bookingService.getOwnerBookingsWithState(userId, queryStatus);
        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        }
        return bookings.get();
    }

    @GetMapping("")
    public List<BookingDtoFull> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("get user id: %d bookings", userId));
        Optional<List<BookingDtoFull>> bookings = bookingService.getUserBookings(userId);
        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        return bookings.get();
    }

    @GetMapping(path = "", params = "state")
    public List<BookingDtoFull> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = true, defaultValue = "ALL") String state) {
        log.info(String.format("get user id: %d bookings with state %s", userId, state));
        State queryStatus;
        try {
            queryStatus = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }
        queryStatus = State.valueOf(state);
        Optional<List<BookingDtoFull>> bookings = bookingService.getUserBookingsWithState(userId, queryStatus);
        if (bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "state not found");
        }
        return bookings.get();
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        log.info(String.format("delete booking id: %d", bookingId));
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoFull updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId,
                               @RequestParam(required = true) Boolean approved) {
        log.info(String.format("owner id: %d approves booking id %d", userId, bookingId));
        try {
            Optional<BookingDtoFull> updatedBooking = bookingService.updateBooking(userId, bookingId, approved);
            if (updatedBooking.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong booking owner");
            }
            return updatedBooking.get();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "wrong booking owner");
        }
    }

    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d rejects booking id %d", userId, bookingId));
        bookingService.cancelBooking(userId, bookingId);
    }
}
