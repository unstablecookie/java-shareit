package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto getItemRequest(Long itemRequestId);

    void deleteItemRequest(Long itemRequestId);
}
