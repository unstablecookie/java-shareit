package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRequestRepositoryInMemory implements ItemRequestRepository {
    private final Map<Long, ItemRequest> storage = new HashMap<>();
    private Long counter = 1L;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {
        if (itemRequest.getId() == null) {
            itemRequest.setId(counter);
            counter++;
        }
        storage.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public void updateItemRequest(ItemRequest updatedItemRequest) {
        storage.put(updatedItemRequest.getId(), updatedItemRequest);
    }

    @Override
    public ItemRequest getItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = storage.get(itemRequestId);

        return itemRequest;
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        storage.remove(itemRequestId);
    }
}
