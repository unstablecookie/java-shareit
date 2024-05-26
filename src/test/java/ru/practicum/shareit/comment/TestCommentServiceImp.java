package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.BookingNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestCommentServiceImp {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private CommentService commentService;
    private Comment comment;
    private CommentDto commentDto;
    private CommentDtoFull commentDtoFull;
    private User author;
    private Long ownerId = 2L;
    private Long authorId = 1L;
    private Item item;
    private Booking firstBooking;
    private String text = "it's good";
    private Long commentId = 1L;

    @BeforeEach
    private void init() {
        commentService = new CommentServiceImp(commentRepository, bookingRepository, userRepository, itemRepository);
        item = Item.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        author = User.builder()
                .id(authorId)
                .name("Ben")
                .email("bens@mail.ts")
                .build();
        comment = Comment.builder()
                .id(commentId)
                .item(item)
                .text(text)
                .author(author)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1))
                .build();
        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 1, 1, 2, 1, 1))
                .item(item)
                .user(author)
                .status(Status.APPROVED)
                .build();
        commentDto = CommentDto.builder()
                .id(commentId)
                .text(text)
                .build();
    }

    @Test
    void addItemComment_success() {
        //given
        when(bookingRepository.findUsersBookingForAnItemOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(List.of(firstBooking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        //when
        commentDtoFull = CommentMapper.toCommentDtoFull(comment, author.getName());
        CommentDtoFull savedCommentDtoFull = commentService.addItemComment(authorId, commentDto, item.getId());
        //then
        assertThat(savedCommentDtoFull)
                .isNotNull()
                .isInstanceOf(CommentDtoFull.class)
                .isEqualTo(commentDtoFull);
    }

    @Test
    void addItemComment_failure_bookingWasRejected() {
        //given
        firstBooking.setStatus(Status.REJECTED);
        when(bookingRepository.findUsersBookingForAnItemOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(List.of(firstBooking));
        //when
        commentDtoFull = CommentMapper.toCommentDtoFull(comment, author.getName());
        //then
        assertThrows(BookingNotFoundException.class, () -> commentService.addItemComment(authorId, commentDto, item.getId()));
    }
}
