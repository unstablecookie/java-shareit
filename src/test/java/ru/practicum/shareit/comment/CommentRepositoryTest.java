package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingRepositoryInMemory;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentRepositoryTest {
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private Booking booking;
    private Item item;
    private User user;
    private Comment comment;

    @BeforeEach
    public void init() {
        bookingRepository = new BookingRepositoryInMemory();
        commentRepository = new CommentRepositoryInMemory();
        Long bookingId = 1L;
        LocalDate start = LocalDate.of(2020, 11,11);
        LocalDate end = LocalDate.of(2020, 11,13);
        item = Item.builder()
                .id(1L)
                .name("thing")
                .description("my description")
                .available(Boolean.TRUE)
                .owner(1L)
                .build();
        user = User.builder()
                .id(2L)
                .name("Bob")
                .email("bobs@mail.ru")
                .build();
        booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        comment = Comment.builder()
                .text("such text very comment")
                .build();
    }

    @Test
    void addComment_success() {
        //given
        bookingRepository.addBooking(booking);
        //when
        comment.setItemId(item.getId());
        Comment addedComment = commentRepository.addComment(comment);
        //then
        assertThat(addedComment)
                .isNotNull()
                .isInstanceOf(Comment.class);
        assertThat(addedComment.getId())
                .isNotNull()
                .isInstanceOf(Long.class)
                .isEqualTo(1L);
    }
}
