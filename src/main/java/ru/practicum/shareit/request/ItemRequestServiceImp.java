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
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("user do not exists");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.addItemRequest(itemRequest));
    }

    @Override
    public ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.getUser(userId);
        ItemRequest oldItemRequest = itemRequestRepository.getItemRequest(itemRequestId);
        if ((oldItemRequest == null) || (user == null)) {
            throw new EntityNotFoundException("item request or user do not exists");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        ItemRequest updatedItemRequest = ItemRequestMapper.updateItemRequestWithItemRequest(oldItemRequest, itemRequest);
        itemRequestRepository.updateItemRequest(updatedItemRequest);
        return ItemRequestMapper.toItemRequestDto(updatedItemRequest);
    }

    @Override
    public ItemRequestDto getItemRequest(Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.getItemRequest(itemRequestId);
        if (itemRequest == null) {
            throw new EntityNotFoundException(String.format("item request id %d not found", itemRequestId));
        }
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        itemRequestRepository.deleteItemRequest(itemRequestId);
    }
}
