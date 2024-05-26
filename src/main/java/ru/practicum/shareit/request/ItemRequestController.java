package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
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
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable Long itemRequestId) {
        log.info(String.format("get item request id: %d", itemRequestId));
        return itemRequestService.getItemRequest(userId, itemRequestId);
    }

    @GetMapping("")
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("get user id: %d item requests", userId));
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("get all requests");
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemRequestId) {
        log.info(String.format("delete item request id: %d", itemRequestId));
        itemRequestService.deleteItemRequest(userId, itemRequestId);
    }
}
