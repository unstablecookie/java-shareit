package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long id);

    List<Item> getUserItems(Long userId);

    List<Item> searchItem(String searchText);

    void deleteItem(Long itemId);

    Set<Long> deleteUserItems(Long userId);
}
