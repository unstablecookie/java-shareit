package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.error.StateSubset;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("add new booking");
        return bookingClient.addBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info(String.format("get booking id: %d", bookingId));
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingWithOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get owner id: %d bookings", userId));
        return bookingClient.getBookingWithOwner(userId, from, size);
    }

    @GetMapping(value = "/owner", params = "state")
    public ResponseEntity<Object> getBookingWithOwnerWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @StateSubset(enumClass = State.class)
                                                               @RequestParam(defaultValue = "ALL") String state,
                                                               @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                               @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get owner id: %d bookings with state", userId));
        return bookingClient.getBookingWithOwnerWithState(userId, State.valueOf(state), from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get user id: %d bookings", userId));
        return bookingClient.getUserBookings(userId, from, size);
    }

    @GetMapping(params = "state")
    public ResponseEntity<Object> getUserBookingsWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @StateSubset(enumClass = State.class)
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get user id: %d bookings with state %s", userId, state));
        return bookingClient.getUserBookingsWithState(userId, State.valueOf(state), from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info(String.format("delete booking id: %d", bookingId));
        bookingClient.deleteBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(required = true) Boolean approved) {
        log.info(String.format("owner id: %d approves booking id %d", userId, bookingId));
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d rejects booking id %d", userId, bookingId));
        bookingClient.cancelBooking(userId, bookingId);
    }
}
