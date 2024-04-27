package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository {
    ItemRequest addItemRequest(ItemRequest itemRequest);

    ItemRequest updateItemRequest(Long itemRequestId, ItemRequest itemRequest);

    ItemRequest getItemRequest(Long itemRequestId);

    void deleteItemRequest(Long itemRequestId);
}
