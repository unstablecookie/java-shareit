package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .build();
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description((itemRequestDto.getDescription() != null) ? itemRequestDto.getDescription() : null)
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
                .build();
        return updatedItemRequest;
    }
}
