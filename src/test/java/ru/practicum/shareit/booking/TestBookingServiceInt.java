package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
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
public class TestBookingServiceInt {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingFullDto bookingFullDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto ownerDto;
    private User user;
    private User owner;
    private Item item;
    private Booking firstBooking;
    private Long userId = 1L;
    private Long itemId = 1L;
    private Long ownerId = 2L;
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .build();
        userDto = UserDto.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(1L)
                .build();
        bookingFullDto = BookingFullDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(Status.WAITING)
                .build();
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();
        ownerDto = UserMapper.toUserDto(owner);
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        firstBooking = BookingMapper.toBooking(bookingDto, item, user);
        userService.addUser(userId, userDto);
        userService.addUser(ownerId, ownerDto);
        itemService.addItem(ownerId, itemDto);
    }

    @Test
    void addBooking_success() {
        //given
        firstBooking.setId(1L);
        //when
        bookingService.addBooking(bookingDto, userId);
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b", Booking.class)
                .getSingleResult();
        //then
        assertThat(queryBooking)
                .isNotNull()
                .isInstanceOf(Booking.class)
                .isEqualTo(firstBooking);
    }

    @Test
    void getBooking_success() {
        //given
        bookingService.addBooking(bookingDto, userId);
        Long bookingId = 1L;
        //when
        BookingFullDto returnedBookingFullDto = bookingService.getBooking(bookingId, userId);
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b where b.id = :id", Booking.class)
                .setParameter("id", bookingId)
                .getSingleResult();
        //then
        assertThat(returnedBookingFullDto)
                .isNotNull()
                .isInstanceOf(BookingFullDto.class)
                .isEqualTo(bookingFullDto);
        assertThat(returnedBookingFullDto)
                .isEqualTo(BookingMapper.toBookingDtoFull(queryBooking));
    }

    @Test
    void getOwnerBookings_success() {
        //given
        bookingService.addBooking(bookingDto, userId);
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(ownerId, defaultFrom, defaultSize);
        List<Booking> queryBookings =
                entityManager.createQuery("select b from Booking as b join b.item as i where i.owner = :id", Booking.class)
                        .setParameter("id", ownerId)
                                .getResultList();
        List<BookingFullDto> queryBookingsFullDto = queryBookings.stream()
                        .map(x -> BookingMapper.toBookingDtoFull(x))
                                .collect(Collectors.toList());
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0))
                .isEqualTo(queryBookingsFullDto.get(0));
    }

    @Test
    void getOwnerBookingsWithState_success() {
        //given
        bookingDto.setStatus(Status.REJECTED);
        bookingService.addBooking(bookingDto, userId);
        //when
        List<BookingFullDto> ownerBookingsRejected = bookingService.getOwnerBookingsWithState(ownerId, State.REJECTED,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsRejected =
                entityManager.createQuery("select b from Booking as b join b.item as i " +
                                "where i.owner = :id and b.status = :status", Booking.class)
                        .setParameter("id", ownerId)
                        .setParameter("status", Status.REJECTED)
                        .getResultList();
        List<BookingFullDto> queryBookingsRejectedFullDto = queryBookingsRejected.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .collect(Collectors.toList());
        List<BookingFullDto> ownerBookingsWaiting = bookingService.getOwnerBookingsWithState(ownerId, State.WAITING,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsWaiting =
                entityManager.createQuery("select b from Booking as b join b.item as i " +
                                "where i.owner = :id and b.status = :status", Booking.class)
                        .setParameter("id", ownerId)
                        .setParameter("status", Status.WAITING)
                        .getResultList();
                //then
        assertThat(ownerBookingsRejected)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookingsRejected.get(0))
                .isEqualTo(queryBookingsRejectedFullDto.get(0));
        assertThat(ownerBookingsWaiting)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
        assertThat(queryBookingsWaiting)
                .hasSize(0);
    }

    @Test
    void getUserBookingsWithState_success() {
        //given
        bookingDto.setStatus(Status.REJECTED);
        bookingService.addBooking(bookingDto, userId);
        //when
        List<BookingFullDto> userBookingsRejected = bookingService.getUserBookingsWithState(userId, State.REJECTED,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsRejected =
                entityManager.createQuery("select b from Booking as b " +
                                "where b.user = :user and b.status = :status", Booking.class)
                        .setParameter("user", user)
                        .setParameter("status", Status.REJECTED)
                        .getResultList();
        List<BookingFullDto> queryBookingsRejectedFullDto = queryBookingsRejected.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .collect(Collectors.toList());
        List<BookingFullDto> userBookingsWaiting = bookingService.getUserBookingsWithState(userId, State.WAITING,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsWaiting =
                entityManager.createQuery("select b from Booking as b " +
                                "where b.user = :user and b.status = :status", Booking.class)
                        .setParameter("user", user)
                        .setParameter("status", Status.WAITING)
                        .getResultList();
        //then
        assertThat(userBookingsRejected)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(userBookingsRejected.get(0))
                .isEqualTo(queryBookingsRejectedFullDto.get(0));
        assertThat(userBookingsWaiting)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
        assertThat(userBookingsWaiting)
                .hasSize(0);
    }

    @Test
    void getUserBookings_success() {
        //given
        bookingService.addBooking(bookingDto, userId);
        //when
        List<BookingFullDto> userBookings = bookingService.getUserBookings(userId, defaultFrom, defaultSize);
        List<Booking> queryBookings =
                entityManager.createQuery("select b from Booking as b where b.user = :user", Booking.class)
                        .setParameter("user", user)
                        .getResultList();
        List<BookingFullDto> queryBookingsFullDto = queryBookings.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .collect(Collectors.toList());
        //then
        assertThat(userBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(userBookings.get(0))
                .isEqualTo(queryBookingsFullDto.get(0));
    }

    @Test
    void deleteBooking_success() {
        //given
        bookingService.addBooking(bookingDto, userId);
        Long bookingId = 1L;
        //when
        bookingService.deleteBooking(bookingId);
        List<Booking> bookings = entityManager.createQuery("SELECT b FROM Booking b", Booking.class)
                        .getResultList();
        //then
        assertThat(bookings)
                .hasSize(0);
    }

    @Test
    void updateBooking_success() {
        //given
        Long bookingId = 1L;
        bookingService.addBooking(bookingDto, userId);
        //when
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b where b.id = :id", Booking.class)
                .setParameter("id", bookingId)
                .getSingleResult();
        BookingFullDto updatedBookingFullDto = bookingService.updateBooking(ownerId, bookingId, Boolean.TRUE);
        //then
        assertThat(BookingMapper.toBookingDtoFull(queryBooking))
                .isEqualTo(updatedBookingFullDto);
    }

    @Test
    void cancelBooking_success() {
        //given
        Long bookingId = 1L;
        bookingService.addBooking(bookingDto, userId);
        //when
        bookingService.cancelBooking(userId, bookingId);
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b where b.id = :id", Booking.class)
                .setParameter("id", bookingId)
                .getSingleResult();
        //then
        assertThat(queryBooking)
                .isNotNull()
                .isInstanceOf(Booking.class);
        assertThat(queryBooking.getStatus())
                .isEqualTo(Status.CANCELED);
    }
}
