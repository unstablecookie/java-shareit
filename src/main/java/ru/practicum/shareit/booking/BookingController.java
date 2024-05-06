package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.model.Comment;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final CommentService commentService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto) {
        log.info("new booking");
        return bookingService.addBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestBody BookingDto bookingDto,
                                    @PathVariable Long bookingId) {
        log.info(String.format("update booking id: %d", bookingId));
        return bookingService.updateBooking(bookingId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId) {
        log.info(String.format("get booking id: %d", bookingId));
        return bookingService.getBooking(bookingId);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        log.info(String.format("delete booking id: %d", bookingId));
        bookingService.deleteBooking(bookingId);
    }

    @PutMapping("/approve/{bookingId}")
    public void approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d approves booking id %d", userId, bookingId));
        bookingService.approveBooking(userId, bookingId);
    }

    @PutMapping("/reject/{bookingId}")
    public void rejectBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d rejects booking id %d", userId, bookingId));
        bookingService.rejectBooking(userId, bookingId);
    }

    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info(String.format("owner id: %d rejects booking id %d", userId, bookingId));
        bookingService.cancelBooking(userId, bookingId);
    }

    @PutMapping("/comment/{bookingId}")
    public Comment commentBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                               @Valid @RequestBody Comment comment) {
        log.info(String.format("leaving comment for booking id: %d ", bookingId));
        return commentService.addComment(userId, bookingId, comment);
    }
}
