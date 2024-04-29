package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(Long userId, ItemDto itemDto) {
        Item item = Item.builder()
                .id((itemDto.getId() != null) ? itemDto.getId() : null)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available((itemDto.getAvailable() != null) ? itemDto.getAvailable() : true)
                .owner(userId)
                .build();
        return item;
    }

    public static Item updateItemWithItem(Item oldItem, Item newItem) {
        Item updatedItem = Item.builder()
                .id(oldItem.getId())
                .name((newItem.getName() != null) ? newItem.getName() : oldItem.getName())
                .description((newItem.getDescription() != null) ? newItem.getDescription() : oldItem.getDescription())
                .available((newItem.getAvailable() != null) ? newItem.getAvailable() : oldItem.getAvailable())
                .owner((newItem.getOwner() != null) ? newItem.getOwner() : oldItem.getOwner())
                .request((newItem.getRequest() != null) ? newItem.getRequest() : oldItem.getRequest())
                .build();
        return updatedItem;
    }

    public static Set<ItemDto> allItemsToItemsDto(Set<Item> items) {
        return items.stream().map(x -> toItemDto(x)).collect(Collectors.toSet());
    }
}
