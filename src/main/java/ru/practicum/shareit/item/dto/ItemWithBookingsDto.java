package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
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
    private LastBookingDto lastBooking;
    private NextBookingDto nextBooking;
    private List<CommentDtoFull> comments = new ArrayList<>();
}
