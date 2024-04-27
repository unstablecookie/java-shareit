package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;

@Service
@AllArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        userRepository.getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.getUser(userId));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.addItemRequest(itemRequest));
    }

    @Override
    public ItemRequestDto updateItemRequest(Long itemRequestId, ItemRequestDto itemRequestDto, Long userId) {
        userRepository.getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.getUser(userId));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.updateItemRequest(itemRequestId, itemRequest));
    }

    @Override
    public ItemRequestDto getItemRequest(Long itemRequestId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.getItemRequest(itemRequestId));
    }

    @Override
    public void deleteItemRequest(Long itemRequestId) {
        itemRequestRepository.deleteItemRequest(itemRequestId);
    }
}
