package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description((itemRequestDto.getDescription() != null) ? itemRequestDto.getDescription() : null)
                .created((itemRequestDto.getCreated() != null) ? itemRequestDto.getCreated() : null)
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
                .created((itemRequest.getCreated() != null) ?
                        itemRequest.getCreated() : oldItemRequest.getCreated())
                .build();
        return updatedItemRequest;
    }
}
