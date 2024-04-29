package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.TimeOverlapException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Comment addComment(Long userId, Long bookingId, Comment comment) {
        Booking booking = bookingRepository.getBooking(bookingId);
        if ((booking == null) || (booking.getBooker().getId() != userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "wrong booking id or user");
        }
        if ((booking.getStatus() == Status.APPROVED) && ((booking.getEnd().isBefore(LocalDate.now())) || (booking.getEnd().equals(LocalDate.now())))) {
            comment.setItemId(booking.getItem().getId());
            return commentRepository.addComment(comment);
        } else {
            throw new TimeOverlapException("wrong date or booking status");
        }
    }
}
