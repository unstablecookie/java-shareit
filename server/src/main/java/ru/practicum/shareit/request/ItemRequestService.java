package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId);

    ItemRequestDto getItemRequest(Long userId, Long itemRequestId);

    void deleteItemRequest(Long userId, Long itemRequestId);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);
}
