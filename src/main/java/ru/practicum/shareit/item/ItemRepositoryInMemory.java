package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> storage = new HashMap<>();
    private final Map<Long, Set<Long>> itemsOfUsers = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Item addItem(Item item) {
        item.setId(counter);
        counter++;
        storage.put(item.getId(), item);
        if (itemsOfUsers.get(item.getOwner()) == null) {
            Set<Long> itemsIds = new HashSet<>();
            itemsIds.add(item.getId());
            itemsOfUsers.put(item.getOwner(), itemsIds);
        } else {
            Set<Long> itemsIds = itemsOfUsers.get(item.getOwner());
            itemsIds.add(item.getId());
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = storage.get(item.getId());
        if (oldItem == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("item id %d not found", item.getId())
            );
        }
        Item updatedItem = ItemMapper.updateItemWithItem(oldItem, item);
        storage.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public Item getItem(Long id) {
        Item item = storage.get(id);
        if (item == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("item id %d not found", id)
            );
        }
        return item;
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        Set<Long> itemIds = itemsOfUsers.get(userId);
        if (itemIds == null) {
            return new ArrayList<>();
        }
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            items.add(getItem(itemId));
        }
        return items;
    }

    @Override
    public List<Item> searchItem(String searchText) {
        return storage.values().stream()
                .filter(x -> x.getAvailable())
                .filter(x -> matchCase(searchText, x.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item = getItem(itemId);
        if (item != null) {
            Long ownerId = item.getOwner();
            Set<Long> items = itemsOfUsers.get(ownerId);
            items.remove(itemId);
        }
        storage.remove(itemId);
    }

    @Override
    public Set<Long> deleteUserItems(Long userId) {
        Set<Long> items = itemsOfUsers.get(userId);
        if (items == null) {
            return new HashSet<>();
        }
        items.stream().forEach(x -> storage.remove(x));
        itemsOfUsers.remove(userId);
        return items;
    }

    private boolean matchCase(String searchWord, String pattern) {
        String[] patterns = pattern.toLowerCase().split(" ");
        for (String word : patterns) {
            if (searchWord.toLowerCase().equals(word)) {
                return true;
            }
            if (searchWord.length() < word.length()) {
                for (int i = 0; i < word.length() - searchWord.length(); i++) {
                    if (searchWord.toLowerCase().equals(word.substring(i, i + searchWord.length()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
