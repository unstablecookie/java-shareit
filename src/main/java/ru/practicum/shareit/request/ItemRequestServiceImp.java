package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@AllArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", userId)));
        ItemRequest oldItemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        ItemRequest updatedItemRequest =
                ItemRequestMapper.updateItemRequestWithItemRequest(oldItemRequest, itemRequest);
        itemRequestRepository.save(updatedItemRequest);
        return ItemRequestMapper.toItemRequestDto(updatedItemRequest);
    }

    @Override
    public ItemRequestDto getItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("request id: %d was not found", itemRequestId)));
        itemRequestRepository.delete(itemRequest);
    }
}
