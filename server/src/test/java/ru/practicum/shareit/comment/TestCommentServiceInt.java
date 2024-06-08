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
    private CommentDto commentDto;
    private User user;
    private BookingDto bookingDto;
    private Item item;

    @BeforeEach
    private void init() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);
        item = createItem();
        item.setId(1L);
        user = createUser("Ken", "eken@mail.ts");
        user.setId(1L);
        commentDto = CommentDto.builder()
                .id(1L)
                .text("it's good")
                .build();
        bookingDto = createBookingDto();
        userService.addUser(createUserDto());
        userService.addUser(UserDto.builder().id(2L).name("Peter").email("iown@mail.ts").build());
        itemService.addItem(2L, createItemDto());
        bookingService.addBooking(bookingDto, user.getId());
    }

    @Test
    void addItemComment_success() {
        //given
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
        String userName = "Ken";
        Long commentId = 1L;
        //when
        CommentDtoFull addedCommentDtoFull = commentService.addItemComment(user.getId(), commentDto, item.getId());
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

    private Item createItem() {
        return Item.builder()
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Ken")
                .email("eken@mail.ts")
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .itemId(1L)
                .bookerId(1L)
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .build();
    }
}
