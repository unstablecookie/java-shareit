package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.TimeOverlapException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
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
        assertThat(addedBooking.getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void addBooking_failure_timeOverlap() {
        //given
        bookingRepository.addBooking(booking);
        //when
        User anotherUser = User.builder()
                .id(3L)
                .name("Ken")
                .email("kens@mail.ru")
                .build();
        Booking newBooking = Booking.builder()
                .id(2L)
                .start(LocalDate.of(2020, 11, 12))
                .end(LocalDate.of(2020, 11, 13))
                .item(item)
                .booker(anotherUser)
                .build();
        //then
        assertThatThrownBy(() ->
                bookingRepository.addBooking(newBooking))
                .isInstanceOf(TimeOverlapException.class)
                .hasMessageContaining("Booking for this period already exists");
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
    void updateBooking_failure_timeOverlapWithAnotherBooking() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Long secondBookingId = 2L;
        Booking anotherBooking = Booking.builder()
                .id(secondBookingId)
                .start(LocalDate.of(2020, 11,5))
                .end(LocalDate.of(2020, 11,10))
                .item(item)
                .booker(user)
                .build();
        bookingRepository.addBooking(anotherBooking);
        Booking updateBooking = Booking.builder()
                .end(LocalDate.of(2020, 11, 11))
                .build();
        //then
        assertThatThrownBy(() ->
                bookingRepository.updateBooking(secondBookingId, updateBooking))
                .isInstanceOf(TimeOverlapException.class)
                .hasMessageContaining("Booking for this period already exists");
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
        //then
        assertThatThrownBy(() ->
                bookingRepository.getBooking(wrongId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("booking id -999 not found");
    }

//    @Test
//    void deleteBooking_success() {
//        //given
//        bookingRepository.addBooking(booking);
//        //when
//        bookingRepository.deleteBooking(booking.getId());
//        //then
//        assertThatThrownBy(() ->
//                bookingRepository.getBooking(booking.getId()))
//                .isInstanceOf(ResponseStatusException.class)
//                .hasMessageContaining("booking id 1 not found");
//    }

//    @Test
//    void deleteBooking_failure_wrongBookingId() {
//        //given
//        bookingRepository.addBooking(booking);
//        //when
//        Long wrongId = -999L;
//        //then
//        assertThatThrownBy(() ->
//                bookingRepository.deleteBooking(wrongId))
//                .isInstanceOf(ResponseStatusException.class)
//                .hasMessageContaining("booking id -999 not found");
//    }

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
        //then
        assertThatThrownBy(() ->
                bookingRepository.getBooking(booking.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("booking id 1 not found");
        assertThatThrownBy(() ->
                bookingRepository.getBooking(anotherBooking.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("booking id 2 not found");
    }

    @Test
    void deleteItemBookings_failure_nonExistingItem() {
        //given
        bookingRepository.addBooking(booking);
        //when
        Long wrongItemId = -999L;
        //then
        assertThatThrownBy(() ->
                bookingRepository.deleteItemBookings(wrongItemId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("item id -999 do not exist");
        ;
    }
}
