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
    private PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    private void init() {
        user = createUser("Ken", "eken@mail.ts");
        owner = createUser("Peter", "iown@mail.ts");
        item = createItem();
        booking = createBooking();
        em.persist(user);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
    }

    @Test
    void findUsersBookingForAnItemOrderByStartDesc_success() {
        //when
        List<Booking> bookings = bookingRepository.findUsersBookingForAnItemOrderByStartDesc(user.getId(), item.getId());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findByItemIdOrderByStartDesc_success() {
        //when
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void findAllOwnerBookings_success() {
        //when
        List<Booking> bookings = bookingRepository.findAllOwnerBookings(owner.getId(), page).stream()
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
        List<Booking> bookings = bookingRepository.findAllOwnerBookingsOrderByStartDesc(owner.getId(), page).stream()
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
        List<Booking> bookings = bookingRepository.findAllOwnerBookingsAndStatus(owner.getId(), Status.WAITING, page)
                .stream()
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
        List<Booking> bookings = bookingRepository.findByUserIdAndStatus(user.getId(), Status.WAITING, page).stream()
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
        List<Booking> bookings = bookingRepository.findByOwnerIdAndEndBefore(owner.getId(), beforeEnd, page).stream()
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
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStartAfter(owner.getId(), startAfter, page).stream()
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
        List<Booking> bookings = bookingRepository.findByOwnerIdAndTimeCurrent(owner.getId(), current, page).stream()
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
        List<Booking> bookings = bookingRepository.findByUserIdAndEndBefore(user.getId(), endBefore, page).stream()
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
        List<Booking> bookings = bookingRepository.findByUserIdAndStartAfter(user.getId(), startAfter, page).stream()
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
        List<Booking> bookings = bookingRepository.findByUserIdAndTimeCurrent(user.getId(), current, page).stream()
                .collect(Collectors.toList());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
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

    private Booking createBooking() {
        return Booking.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .item(item)
                .user(user)
                .status(Status.WAITING)
                .build();
    }
}
