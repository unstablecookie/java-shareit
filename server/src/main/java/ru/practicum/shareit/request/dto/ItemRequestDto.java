package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime created;
    private List<ItemDto> items;
}
