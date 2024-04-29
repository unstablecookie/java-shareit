package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Optional<ItemDto> addItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUser(userId);;
        if (user == null) {
            return Optional.empty();
        }
        Item item = ItemMapper.toItem(userId, itemDto);
        ItemDto newItemDto = ItemMapper.toItemDto(itemRepository.addItem(item));
        return Optional.of(newItemDto);
    }

    @Override
    public Optional<ItemDto> updateItem(Long userId, ItemDto itemDto, Long itemId) {
        User user = userRepository.getUser(userId);;
        if (user == null) {
            throw new EntityNotFoundException(String.format("user %d do not exists", userId));
        }
        Item oldItem = itemRepository.getItem(itemId);
        if (oldItem == null) {
            throw new EntityNotFoundException(String.format("item id %d not found", itemId));
        }
        Item item = ItemMapper.toItem(userId, itemDto);
        item.setId(itemId);
        if (validateOwner(userId, item)) {
            return Optional.empty();
        }
        Item updatedItem = ItemMapper.updateItemWithItem(oldItem, item);
        itemRepository.updateItem(updatedItem);
        ItemDto newItemDto = ItemMapper.toItemDto(updatedItem);
        return Optional.of(newItemDto);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.getItem(itemId);
        if (item == null) {
            throw new EntityNotFoundException(String.format("item id %d not found", itemId));
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Set<ItemDto> getUserItems(Long userId) {
        userRepository.getUser(userId);
        Set<Item> items = itemRepository.getUserItems(userId);
        return ItemMapper.allItemsToItemsDto(items);
    }

    @Override
    public Set<ItemDto> searchItem(String searchText) {
        if (searchText.length() < 1) {
            return Set.of();
        }
        Set<Item> items = itemRepository.getItems();
        return ItemMapper.allItemsToItemsDto(items.stream()
                .filter(x -> x.getAvailable())
                .filter(x -> matchCase(searchText, x.getDescription()))
                .collect(Collectors.toSet()));
    }

    @Override
    public void deleteItem(Long itemId) {
        bookingRepository.deleteItemBookings(itemId);
        Item item = itemRepository.getItem(itemId);
        itemRepository.deleteItem(item);
    }

    @Override
    public void deleteUserItems(Long userId) {
        userRepository.getUser(userId);
        itemRepository.deleteUserItems(userId);
    }

    private boolean validateOwner(Long userId, Item item) {
        Item oldItem = itemRepository.getItem(item.getId());
        return !itemRepository.getItem(item.getId()).getOwner().equals(userId);
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
