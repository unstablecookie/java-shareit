package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<ItemDto> addItem(Long userId, ItemDto itemDto);

    Optional<ItemDto> updateItem(Long userId, ItemDto itemDto, Long itemId);

    Optional<ItemWithBookingsDto> getItem(Long userId, Long itemId);

    List<ItemWithBookingsDto> getUserItems(Long userId);

    List<ItemDto> searchItem(String searchText);

    void deleteItem(Long itemId);

    void deleteUserItems(Long userId);
}
