package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemRepository {
    Item addItem(Item item);

    void updateItem(Item item);

    Item getItem(Long id);

    Set<Item> getUserItems(Long userId);

    Set<Item> getItems();

    void deleteItem(Item item);

    void deleteUserItems(Long userId);
}
