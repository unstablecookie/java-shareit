package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.UnsupportedStatusException;
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
    private CommentDtoFull commentDtoFull;
    private User author;
    private Item item;
    private Booking booking;

    @BeforeEach
    private void init() {
        commentService = new CommentServiceImp(commentRepository, bookingRepository, userRepository, itemRepository);
        item = createItem();
        item.setId(1L);
        author = createUser("Ben", "bens@mail.ts");
        author.setId(1L);
        comment = Comment.builder().id(1L).item(item).text("it's good").author(author)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1)).build();
        booking = BookingMapper.toBooking(createBookingDto(), item, author);
    }

    @Test
    void addItemComment_success() {
        //given
        CommentDto commentDto = createCommentDto();
        when(bookingRepository.findUsersBookingForAnItemOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        //when
        commentDtoFull = CommentMapper.toCommentDtoFull(comment, author.getName());
        CommentDtoFull savedCommentDtoFull = commentService.addItemComment(author.getId(), commentDto, item.getId());
        //then
        assertThat(savedCommentDtoFull)
                .isNotNull()
                .isInstanceOf(CommentDtoFull.class)
                .isEqualTo(commentDtoFull);
    }

    @Test
    void addItemComment_failure_noBookings() {
        //given
        CommentDto commentDto = createCommentDto();
        //when
        when(bookingRepository.findUsersBookingForAnItemOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(List.of());
        //then
        assertThrows(UnsupportedStatusException.class, () -> commentService.addItemComment(author.getId(), commentDto,
                item.getId()));
    }

    @Test
    void addItemComment_failure_bookingWasRejected() {
        //given
        CommentDto commentDto = createCommentDto();
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findUsersBookingForAnItemOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        //when
        commentDtoFull = CommentMapper.toCommentDtoFull(comment, author.getName());
        //then
        assertThrows(UnsupportedStatusException.class, () -> commentService.addItemComment(author.getId(), commentDto,
                item.getId()));
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 1, 1, 2, 1, 1))
                .itemId(1L)
                .bookerId(1L)
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("user comment")
                .build();
    }
}
