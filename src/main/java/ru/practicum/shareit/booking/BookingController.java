package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.error.StateSubset;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Valid @RequestBody BookingDto bookingDto) {
        log.info("add new booking");
        return bookingService.addBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info(String.format("get booking id: %d", bookingId));
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getBookingWithOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get owner id: %d bookings", userId));
        List<BookingFullDto> bookings = bookingService.getOwnerBookings(userId, from, size);
        return bookings;
    }

    @GetMapping(value = "/owner", params = "state")
    public List<BookingFullDto> getBookingWithOwnerWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @StateSubset(enumClass = State.class)
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                             @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get owner id: %d bookings with state", userId));
        List<BookingFullDto> bookings = bookingService.getOwnerBookingsWithState(userId, State.valueOf(state), from, size);
        return bookings;
    }

    @GetMapping("")
    public List<BookingFullDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get user id: %d bookings", userId));
        List<BookingFullDto> bookings = bookingService.getUserBookings(userId, from, size);
        return bookings;
    }

    @GetMapping(path = "", params = "state")
    public List<BookingFullDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @StateSubset(enumClass = State.class)
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info(String.format("get user id: %d bookings with state %s", userId, state));
        List<BookingFullDto> bookings = bookingService.getUserBookingsWithState(userId, State.valueOf(state), from, size);
        return bookings;
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        log.info(String.format("delete booking id: %d", bookingId));
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam(required = true) Boolean approved) {
        log.info(String.format("owner id: %d approves booking id %d", userId, bookingId));
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d rejects booking id %d", userId, bookingId));
        bookingService.cancelBooking(userId, bookingId);
    }
}
