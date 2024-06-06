package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .start((bookingDto.getStart() != null) ? bookingDto.getStart() : null)
                .end((bookingDto.getEnd() != null) ? bookingDto.getEnd() : null)
                .item((item != null) ? item : null)
                .user((booker != null) ? booker : null)
                .status((bookingDto.getStatus() != null) ? bookingDto.getStatus() : Status.WAITING)
                .build();
    }

    public static BookingFullDto toBookingDtoFull(Booking booking) {
        return BookingFullDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getUser()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingMinDto toMinBookingDto(Booking booking) {
        BookingMinDto minBookingDto = BookingMinDto.builder()
                .id(booking.getId())
                .bookerId(booking.getUser().getId())
                .build();
        return minBookingDto;
    }
}
