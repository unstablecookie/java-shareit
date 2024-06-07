package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = ItemRequest.builder()
                .name((itemRequestDto.getName() != null) ? itemRequestDto.getName() : null)
                .description((itemRequestDto.getDescription() != null) ? itemRequestDto.getDescription() : null)
                .available(itemRequestDto.getAvailable() != null ? itemRequestDto.getAvailable() : Boolean.FALSE)
                .requestor(user)
                .created(itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now())
                .build();
        return itemRequest;
    }

    public static ItemRequest updateItemRequestWithItemRequest(ItemRequest oldItemRequest, ItemRequest itemRequest) {
        ItemRequest updatedItemRequest = ItemRequest.builder()
                .id(oldItemRequest.getId())
                .description((itemRequest.getDescription() != null) ?
                        itemRequest.getDescription() : oldItemRequest.getDescription())
                .requestor((itemRequest.getRequestor() != null) ?
                        itemRequest.getRequestor() : oldItemRequest.getRequestor())
                .name((itemRequest.getName() != null) ?
                        itemRequest.getName() : oldItemRequest.getName())
                .available((itemRequest.getAvailable() != null) ?
                        itemRequest.getAvailable() : oldItemRequest.getAvailable())
                .created((itemRequest.getCreated() != null) ?
                        itemRequest.getCreated() : oldItemRequest.getCreated())
                .build();
        return updatedItemRequest;
    }
}
