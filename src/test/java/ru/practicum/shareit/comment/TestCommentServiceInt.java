package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = { "spring.datasource.driverClassName=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=test",
                "spring.datasource.password=test"}
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestCommentServiceInt {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;
    private final UserService userService;
    private final CommentService commentService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private Comment comment;
    private CommentDto commentDto;
    private User user;
    private User owner;
    private UserDto userDto;
    private UserDto ownerDto;
    private ItemDto itemDto;
    private BookingDto bookingDto;
    private Long ownerId = 2L;
    private Long userId = 1L;
    private Long itemId = 1L;
    private Item item;
    private String text = "it's good";
    private Long commentId = 1L;
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private String itemName = "thing";
    private String itemDescription = "very thing";

    @BeforeEach
    private void init() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);
        item = Item.builder()
                .id(1L)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .item(item)
                .text(text)
                .author(user)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1))
                .build();
        commentDto = CommentDto.builder()
                .id(commentId)
                .text(text)
                .build();
        userDto = UserDto.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .build();
        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(1L)
                .build();
        ownerDto = UserMapper.toUserDto(owner);
        userService.addUser(userId, userDto);
        userService.addUser(ownerId, ownerDto);
        itemService.addItem(ownerId, itemDto);
        bookingService.addBooking(bookingDto, userId);
    }

    @Test
    void addItemComment_success() {
        //given
        Long bookingId = 1L;
        LocalDateTime startInPast = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        LocalDateTime endInPast = LocalDateTime.of(2024, 1, 1, 2, 1, 1);
        Booking bookingInPast = Booking.builder()
                        .start(startInPast)
                                .end(endInPast)
                                        .item(item)
                                                .user(user)
                                                        .status(Status.APPROVED)
                                                                .build();
        entityManager.persist(bookingInPast);
        //when
        CommentDtoFull addedCommentDtoFull = commentService.addItemComment(userId, commentDto, itemId);
        Comment comment = entityManager.createQuery("SELECT c FROM Comment c where c.id = :id", Comment.class)
                .setParameter("id", commentId)
                .getSingleResult();
        CommentDtoFull queryCommentDtoFull = CommentMapper.toCommentDtoFull(comment, userName);
        //then
        assertThat(addedCommentDtoFull)
                .isNotNull()
                .isInstanceOf(CommentDtoFull.class)
                .isEqualTo(queryCommentDtoFull);
    }
}
