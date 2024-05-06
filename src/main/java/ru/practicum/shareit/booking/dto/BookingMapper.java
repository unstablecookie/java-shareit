package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .start((bookingDto.getStart() != null) ? bookingDto.getStart() : null)
                .end((bookingDto.getEnd() != null) ? bookingDto.getEnd() : null)
                .item((item != null) ? item : null)
                .booker((booker != null) ? booker : null)
                .status((bookingDto.getStatus() != null) ? bookingDto.getStatus() : null)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getId())
                .booker(booking.getBooker().getId())
                .status(booking.getStatus())
                .status(booking.getStatus())
                .build();
    }

    public static Booking updateBookingWithBooking(Booking oldBooking, Booking newBooking) {
        Booking updatedBooking = Booking.builder()
                .id(oldBooking.getId())
                .start((newBooking.getStart() != null) ? newBooking.getStart() : oldBooking.getStart())
                .end((newBooking.getEnd() != null) ? newBooking.getEnd() : oldBooking.getEnd())
                .item((newBooking.getItem() != null) ? newBooking.getItem() : oldBooking.getItem())
                .booker((newBooking.getBooker() != null) ? newBooking.getBooker() : oldBooking.getBooker())
                .build();
        return updatedBooking;
    }
}
