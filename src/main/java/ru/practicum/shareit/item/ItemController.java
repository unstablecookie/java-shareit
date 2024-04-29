package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info(String.format("add item for user %d", userId));
        Optional<ItemDto> addedItem = itemService.addItem(userId, itemDto);
        if (addedItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user id %d does not exist");
        }
        return addedItem.get();
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("update item");
        Optional<ItemDto> updatedItemDto = itemService.updateItem(userId, itemDto, itemId);
        if (updatedItemDto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user id %d not owner or booking does not exist");
        }
        return updatedItemDto.get();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable Long itemId) {
        log.info(String.format("get item id %d", itemId));
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public Set<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("get user items");
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Set<ItemDto> searchForAnItem(@RequestParam String text) {
        log.info(String.format("get user items", text));
        return itemService.searchItem(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info(String.format("delete item id %d", itemId));
        itemService.deleteItem(itemId);
    }
}
