package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    ItemWithBookingsDto getItem(Long userId, Long itemId);

    List<ItemWithBookingsDto> getUserItems(Long userId);

    List<ItemDto> searchItem(String searchText);

    void deleteItem(Long itemId);
}
