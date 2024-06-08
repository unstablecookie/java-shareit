package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info(String.format("add item request from user %d", userId));
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @PatchMapping("/{itemRequestId}")
    public ResponseEntity<Object> updateItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto,
                                                    @PathVariable Long itemRequestId) {
        log.info(String.format("update item request id: %d", itemRequestId));
        return itemRequestClient.updateItemRequest(userId, itemRequestDto, itemRequestId);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long itemRequestId) {
        log.info(String.format("get item request id: %d", itemRequestId));
        return itemRequestClient.getItemRequest(userId, itemRequestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(String.format("get user id: %d item requests", userId));
        return itemRequestClient.getItemRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        log.info("get all requests");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemRequestId) {
        log.info(String.format("delete item request id: %d", itemRequestId));
        itemRequestClient.deleteItemRequest(userId, itemRequestId);
    }
}
