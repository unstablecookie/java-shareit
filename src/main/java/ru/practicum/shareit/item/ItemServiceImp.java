package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userRepository.getUser(userId);;
        Item item = ItemMapper.toItem(userId, itemDto);
        ItemDto newItemDto = ItemMapper.toItemDto(itemRepository.addItem(item));
        return newItemDto;
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        userRepository.getUser(userId);
        Item item = ItemMapper.toItem(userId, itemDto);
        item.setId(itemId);
        validateOwner(userId, item);
        ItemDto newItemDto = ItemMapper.toItemDto(itemRepository.updateItem(item));
        return newItemDto;
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.getItem(itemId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userRepository.getUser(userId);
        List<Item> items = itemRepository.getUserItems(userId);
        return ItemMapper.allItemsToItemsDto(items);
    }

    @Override
    public List<ItemDto> searchItem(String searchText) {
        if (searchText.length() < 1) {
            return List.of();
        }
        List<Item> items = itemRepository.searchItem(searchText);
        return ItemMapper.allItemsToItemsDto(items);
    }

    @Override
    public void deleteItem(Long itemId) {
        bookingRepository.deleteItemBookings(itemId);
        itemRepository.deleteItem(itemId);
    }

    @Override
    public void deleteUserItems(Long userId) {
        userRepository.getUser(userId);
        Set<Long> items = itemRepository.deleteUserItems(userId);
        items.stream().forEach(x -> bookingRepository.deleteItemBookings(x));
    }

    private void validateOwner(Long userId, Item item) {
        Item oldItem = itemRepository.getItem(item.getId());
        if (itemRepository.getItem(item.getId()).getOwner() != userId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("wrong owner id: %d",userId)
            );
        }
    }
}
