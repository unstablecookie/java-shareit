package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @PatchMapping("/{itemRequestId}")
    public ItemRequestDto updateItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto,
                                            @PathVariable Long itemRequestId) {
        return itemRequestService.updateItemRequest(itemRequestId, itemRequestDto, userId);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable Long itemRequestId) {
        return itemRequestService.getItemRequest(userId, itemRequestId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemRequestId) {
        itemRequestService.deleteItemRequest(userId, itemRequestId);
    }
}
