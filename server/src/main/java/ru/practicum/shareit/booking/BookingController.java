package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.State;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getBookingWithOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam int from,
                                                    @RequestParam int size) {
        List<BookingFullDto> bookings = bookingService.getOwnerBookings(userId, from, size);
        return bookings;
    }

    @GetMapping(value = "/owner", params = "state")
    public List<BookingFullDto> getBookingWithOwnerWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam String state,
                                                             @RequestParam int from,
                                                             @RequestParam int size) {
        List<BookingFullDto> bookings = bookingService.getOwnerBookingsWithState(userId, State.valueOf(state), from, size);
        return bookings;
    }

    @GetMapping
    public List<BookingFullDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        List<BookingFullDto> bookings = bookingService.getUserBookings(userId, from, size);
        return bookings;
    }

    @GetMapping(params = "state")
    public List<BookingFullDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        List<BookingFullDto> bookings = bookingService.getUserBookingsWithState(userId, State.valueOf(state), from, size);
        return bookings;
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        bookingService.cancelBooking(userId, bookingId);
    }
}
