package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("user do not exists");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user.get());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<ItemRequest> oldItemRequest = itemRequestRepository.findById(itemRequestId);
        if (oldItemRequest.isEmpty() || user.isEmpty()) {
            throw new EntityNotFoundException("item request or user do not exists");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user.get());
        ItemRequest updatedItemRequest =
                ItemRequestMapper.updateItemRequestWithItemRequest(oldItemRequest.get(), itemRequest);
        itemRequestRepository.save(updatedItemRequest);
        return ItemRequestMapper.toItemRequestDto(updatedItemRequest);
    }

    @Override
    public ItemRequestDto getItemRequest(Long itemRequestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
        if (itemRequest.isEmpty()) {
            throw new EntityNotFoundException(String.format("item request id %d not found", itemRequestId));
        }
        return ItemRequestMapper.toItemRequestDto(itemRequest.get());
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemRequestId);
        if (itemRequest.isEmpty()) {
            throw new EntityNotFoundException(String.format("item request id %d not found", itemRequestId));
        }
        itemRequestRepository.delete(itemRequest.get());
    }
}
