package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Optional;
import java.util.Set;

public interface ItemService {
    Optional<ItemDto> addItem(Long userId, ItemDto itemDto);

    Optional<ItemDto> updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getItem(Long userId, Long itemId);

    Set<ItemDto> getUserItems(Long userId);

    Set<ItemDto> searchItem(String searchText);

    void deleteItem(Long itemId);

    void deleteUserItems(Long userId);
}
