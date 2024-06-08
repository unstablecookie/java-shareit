package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingMinDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingMinDto lastBooking;
    private BookingMinDto nextBooking;
    private List<CommentDtoFull> comments = new ArrayList<>();
}
