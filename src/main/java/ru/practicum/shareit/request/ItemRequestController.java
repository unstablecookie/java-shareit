package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info(String.format("add item request from user %d", userId));
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @PatchMapping("/{itemRequestId}")
    public ItemRequestDto updateItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto,
                                            @PathVariable Long itemRequestId) {
        log.info(String.format("update item request id: %d", itemRequestId));
        return itemRequestService.updateItemRequest(itemRequestId, itemRequestDto, userId);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequest(@PathVariable Long itemRequestId) {
        log.info(String.format("get item request id: %d", itemRequestId));
        return itemRequestService.getItemRequest(itemRequestId);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemRequestId) {
        log.info(String.format("delete item request id: %d", itemRequestId));
        itemRequestService.deleteItemRequest(itemRequestId);
    }
}
