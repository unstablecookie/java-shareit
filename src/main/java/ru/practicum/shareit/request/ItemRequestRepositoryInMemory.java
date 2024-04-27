package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRequestRepositoryInMemory implements ItemRequestRepository {
    private final Map<Long, ItemRequest> storage = new HashMap<>();
    private Long counter = 1L;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {
        itemRequest.setId(counter);
        counter++;
        storage.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest updateItemRequest(Long itemRequestId, ItemRequest itemRequest) {
        ItemRequest oldItemRequest = storage.get(itemRequestId);
        if (oldItemRequest == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("item request id %d do not exists", itemRequestId)
            );
        }
        ItemRequest updatedItemRequest = ItemRequestMapper.updateItemRequestWithItemRequest(oldItemRequest, itemRequest);
        storage.put(updatedItemRequest.getId(), updatedItemRequest);
        return updatedItemRequest;
    }

    @Override
    public ItemRequest getItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = storage.get(itemRequestId);
        if (itemRequest == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("item request id %d not found", itemRequestId)
            );
        }
        return itemRequest;
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        storage.remove(itemRequestId);
    }
}
