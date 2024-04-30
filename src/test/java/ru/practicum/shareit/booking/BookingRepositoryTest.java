package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingRepositoryTest {
    private BookingRepository bookingRepository;
    private Booking booking;
    private Item item;
    private User user;

    @BeforeEach
    public void init() {
        bookingRepository = new BookingRepositoryInMemory();
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
                .build();
    }

    @Test
    void addBooking_success() {
        //when
        Booking addedBooking = bookingRepository.addBooking(booking);
        //then
        assertThat(addedBooking)
                .isNotNull()
                .isInstanceOf(Booking.class)
                .isEqualTo(booking);
    }

    @Test
    void updateBooking_success() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Booking updateBooking = Booking.builder()
                .end(LocalDate.of(2020, 11, 15))
                .build();
        bookingRepository.updateBooking(booking.getId(), updateBooking);
        //then
        Booking updatedBooking = bookingRepository.getBooking(booking.getId());
        assertThat(updatedBooking.getEnd().equals(LocalDate.of(2020, 11, 15)));
    }

    @Test
    void getBooking_success() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Booking retrievedBooking = bookingRepository.getBooking(booking.getId());
        //then
        assertThat(retrievedBooking)
                .isNotNull()
                .isInstanceOf(Booking.class)
                .isEqualTo(booking);
    }

    @Test
    void getBooking_failure_withWrongId() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Long wrongId = -999L;
        Booking wrongBooking = bookingRepository.getBooking(wrongId);
        //then
        assertThat(wrongBooking)
                .isNull();
    }

    @Test
    void deleteItemBookings_success() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Long secondBookingId = 2L;
        Booking anotherBooking = Booking.builder()
                .id(secondBookingId)
                .start(LocalDate.of(2020, 11,25))
                .end(LocalDate.of(2020, 11,27))
                .item(item)
                .booker(user)
                .build();
        bookingRepository.addBooking(anotherBooking);
        bookingRepository.deleteItemBookings(item.getId());
        Set<Booking> bookings = bookingRepository.getItemBookings(item.getId());
        //then
        assertThat(bookings)
                .isNotNull()
                .isInstanceOf(Set.class)
                .hasSize(0);
    }
}
