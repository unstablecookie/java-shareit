package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest oldItemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        if (!userId.equals(itemRequest.getRequestor().getId())) {
            throw new EntityNotFoundException(String.format("user id: %d is not an author", userId));
        }
        itemRequest.setRequestor(user);
        ItemRequest updatedItemRequest =
                ItemRequestMapper.updateItemRequestWithItemRequest(oldItemRequest, itemRequest);
        itemRequestRepository.save(updatedItemRequest);
        return ItemRequestMapper.toItemRequestDto(updatedItemRequest, getItemsForRequest(itemRequestId));
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long itemRequestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        return ItemRequestMapper.toItemRequestDto(itemRequest, getItemsForRequest(itemRequestId));
    }

    @Override
    public void deleteItemRequest(Long userId, Long itemRequestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format("user id: %d is not a requestor", userId));
        }
        itemRequestRepository.delete(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        Set<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream().collect(Collectors.toSet());
        return requests.stream()
                .sorted((a, b) -> a.getCreated().compareTo(b.getCreated()))
                .map(x -> ItemRequestMapper.toItemRequestDto(x, getItemsForRequest(x.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, page)
                .map(x -> ItemRequestMapper.toItemRequestDto(x, getItemsForRequest(x.getId())))
                .getContent();
    }

    private List<ItemDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(x -> ItemMapper.toItemDto(x))
                .collect(Collectors.toList());
    }
}
