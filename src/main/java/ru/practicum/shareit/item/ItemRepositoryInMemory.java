package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> storage = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Item addItem(Item item) {
        if (item.getId() == null) {
            item.setId(counter);
            counter++;
        }
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void updateItem(Item item) {
        storage.put(item.getId(), item);
    }

    @Override
    public Item getItem(Long id) {
        Item item = storage.get(id);
        return item;
    }

    @Override
    public Set<Item> getUserItems(Long userId) {
        Set<Item> itemIds = storage.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .collect(Collectors.toSet());
        return itemIds;
    }

    @Override
    public Set<Item> getItems() {
        return storage.values().stream().collect(Collectors.toSet());
    }

    @Override
    public void deleteItem(Item item) {
        storage.remove(item.getId());
    }

    @Override
    public void deleteUserItems(Long userId) {
        List<Item> items = List.copyOf(storage.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .collect(Collectors.toList()));
        items.stream().forEach(x -> storage.remove(x.getId()));
    }
}
