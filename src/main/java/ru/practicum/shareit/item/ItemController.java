package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import javax.validation.Valid;
import java.util.List;

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
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoFull addItemComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody CommentDto commentDto,
                                         @PathVariable Long itemId) {
        log.info(String.format("add comment for an item id:%d", itemId));
        CommentDtoFull addedComment = commentService.addItemComment(userId, commentDto, itemId);
        return addedComment;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("update item");
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info(String.format("get item id %d", itemId));
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("get user items");
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchForAnItem(@RequestParam String text,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.info(String.format("search for an item: ", text));
        return itemService.searchItem(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info(String.format("delete item id %d", itemId));
        itemService.deleteItem(itemId);
    }
}
