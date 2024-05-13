package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

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

    @PostMapping("/{itemId}/comment")
    public CommentDtoFull addItemComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody CommentDto commentDto,
                                         @PathVariable Long itemId) {
        log.info(String.format("add comment for an item id:%d", itemId));
        Optional<CommentDtoFull> addedComment = commentService.addItemComment(userId, commentDto, itemId);
        if (addedComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "booking for this item does not exist");
        }
        return addedComment.get();
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
    public ItemWithBookingsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info(String.format("get item id %d", itemId));
        Optional<ItemWithBookingsDto> item = itemService.getItem(userId, itemId);
        if (item.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("user id: %d not found", itemId));
        }
        return item.get();
    }

    @GetMapping
    public List<ItemWithBookingsDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("get user items");
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForAnItem(@RequestParam String text) {
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
