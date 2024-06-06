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
    private User user;
    private User owner;
    private Item item;
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        user = createUser("Ken", "eken@mail.ts");
        owner = createUser("Peter", "iown@mail.ts");
        user.setId(1L);
        owner.setId(2L);
        item = createItem();
        item.setId(1L);
        bookingDto = createBookingDto();
        booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setId(1L);
        userService.addUser(createUserDto());
        userService.addUser(UserDto.builder().id(2L).name("Peter").email("iown@mail.ts").build());
        itemService.addItem(owner.getId(), createItemDto());
    }

    @Test
    void addBooking_success() {
        //when
        bookingService.addBooking(bookingDto, user.getId());
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b", Booking.class)
                .getSingleResult();
        //then
        assertThat(queryBooking)
                .isNotNull()
                .isInstanceOf(Booking.class)
                .isEqualTo(booking);
    }

    @Test
    void getBooking_success() {
        //given
        bookingService.addBooking(bookingDto, user.getId());
        BookingFullDto bookingFullDto = createBookingFullDto();
        //when
        BookingFullDto returnedBookingFullDto = bookingService.getBooking(booking.getId(), user.getId());
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b where b.id = :id", Booking.class)
                .setParameter("id", booking.getId())
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
        bookingService.addBooking(bookingDto, user.getId());
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(owner.getId(), defaultFrom, defaultSize);
        List<Booking> queryBookings =
                entityManager.createQuery("select b from Booking as b join b.item as i where i.owner = :id", Booking.class)
                        .setParameter("id", owner.getId())
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
        bookingService.addBooking(bookingDto, user.getId());
        //when
        List<BookingFullDto> ownerBookingsRejected = bookingService.getOwnerBookingsWithState(owner.getId(), State.REJECTED,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsRejected =
                entityManager.createQuery("select b from Booking as b join b.item as i " +
                                "where i.owner = :id and b.status = :status", Booking.class)
                        .setParameter("id", owner.getId())
                        .setParameter("status", Status.REJECTED)
                        .getResultList();
        List<BookingFullDto> queryBookingsRejectedFullDto = queryBookingsRejected.stream()
                .map(x -> BookingMapper.toBookingDtoFull(x))
                .collect(Collectors.toList());
        List<BookingFullDto> ownerBookingsWaiting = bookingService.getOwnerBookingsWithState(owner.getId(), State.WAITING,
                defaultFrom, defaultSize);
        List<Booking> queryBookingsWaiting =
                entityManager.createQuery("select b from Booking as b join b.item as i " +
                                "where i.owner = :id and b.status = :status", Booking.class)
                        .setParameter("id", owner.getId())
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
        bookingService.addBooking(bookingDto, user.getId());
        //when
        List<BookingFullDto> userBookingsRejected = bookingService.getUserBookingsWithState(user.getId(), State.REJECTED,
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
        List<BookingFullDto> userBookingsWaiting = bookingService.getUserBookingsWithState(user.getId(), State.WAITING,
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
        bookingService.addBooking(bookingDto, user.getId());
        //when
        List<BookingFullDto> userBookings = bookingService.getUserBookings(user.getId(), defaultFrom, defaultSize);
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
        bookingService.addBooking(bookingDto, user.getId());
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
        bookingService.addBooking(bookingDto, user.getId());
        //when
        Booking queryBooking = entityManager.createQuery("SELECT b FROM Booking b where b.id = :id", Booking.class)
                .setParameter("id", bookingId)
                .getSingleResult();
        BookingFullDto updatedBookingFullDto = bookingService.updateBooking(owner.getId(), bookingId, Boolean.TRUE);
        //then
        assertThat(BookingMapper.toBookingDtoFull(queryBooking))
                .isEqualTo(updatedBookingFullDto);
    }

    @Test
    void cancelBooking_success() {
        //given
        Long bookingId = 1L;
        bookingService.addBooking(bookingDto, user.getId());
        //when
        bookingService.cancelBooking(user.getId(), bookingId);
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

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Ken")
                .email("eken@mail.ts")
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

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .itemId(1L)
                .bookerId(1L)
                .build();
    }

    private BookingFullDto createBookingFullDto() {
        return BookingFullDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .item(createItemDto())
                .booker(createUserDto())
                .status(Status.WAITING)
                .build();
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
}
