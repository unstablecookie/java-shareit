package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        properties = { "spring.datasource.driverClassName=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=test",
                "spring.datasource.password=test"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestBookingJpa {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private Booking booking;
    private Item item;
    private User user;
    private User owner;
    private Long itemId = 1L;
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private Long bookingId = 1L;
    private Long userId = 1L;
    private Long ownerId = 2L;
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private PageRequest page = PageRequest.of(0, 10);
    LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
    LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);

    @BeforeEach
    private void init() {
        user = User.builder()
                .name(userName)
                .email(userEmail)
                .build();
        owner = User.builder()
                .name(ownerName)
                .email(ownerEmail)
                .build();
        item = Item.builder()
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .user(user)
                .status(Status.WAITING)
                .build();
        em.persist(user);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
    }

    @Test
    void findUsersBookingForAnItemOrderByStartDesc_success() {
        //when
        List<Booking> bookings = bookingRepository.findUsersBookingForAnItemOrderByStartDesc(userId, itemId);
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        em.remove(user);
        em.remove(owner);
        em.remove(item);
        em.remove(booking);
    }

    @Test
    void findByItemIdOrderByStartDesc_success() {
        //when
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findAllOwnerBookings_success() {
        //when
        List<Booking> bookings = bookingRepository.findAllOwnerBookings(ownerId, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findAllOwnerBookingsOrderByStartDesc_success() {
        //when
        List<Booking> bookings = bookingRepository.findAllOwnerBookingsOrderByStartDesc(ownerId, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findAllOwnerBookingsAndStatus_success() {
        //when
        List<Booking> bookings = bookingRepository.findAllOwnerBookingsAndStatus(ownerId, Status.WAITING, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByUserIdAndStatus_success() {
        //when
        List<Booking> bookings = bookingRepository.findByUserIdAndStatus(userId, Status.WAITING, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByOwnerIdAndEndBefore_success() {
        //given
        LocalDateTime beforeEnd = LocalDateTime.of(2025, 1, 1, 3, 1, 1);
        //when
        List<Booking> bookings = bookingRepository.findByOwnerIdAndEndBefore(ownerId, beforeEnd, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByOwnerIdAndStartAfter_success() {
        //given
        LocalDateTime startAfter = LocalDateTime.of(2025, 1, 1, 0, 1, 1);
        //when
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStartAfter(ownerId, startAfter, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByOwnerIdAndTimeCurrent_success() {
        //given
        LocalDateTime current = LocalDateTime.of(2025, 1, 1, 1, 30, 1);
        //when
        List<Booking> bookings = bookingRepository.findByOwnerIdAndTimeCurrent(ownerId, current, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByUserIdAndEndBefore_success() {
        //given
        LocalDateTime endBefore = LocalDateTime.of(2025, 2, 1, 1, 1, 1);
        //when
        List<Booking> bookings = bookingRepository.findByUserIdAndEndBefore(userId, endBefore, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByUserIdAndStartAfter_success() {
        //given
        LocalDateTime startAfter = LocalDateTime.of(2024, 2, 1, 1, 1, 1);
        //when
        List<Booking> bookings = bookingRepository.findByUserIdAndStartAfter(userId, startAfter, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByUserIdAndTimeCurrent_success() {
        //given
        LocalDateTime current = LocalDateTime.of(2025, 1, 1, 1, 30, 1);
        //when
        List<Booking> bookings = bookingRepository.findByUserIdAndTimeCurrent(userId, current, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }
    //given
    //when
    //then
}
