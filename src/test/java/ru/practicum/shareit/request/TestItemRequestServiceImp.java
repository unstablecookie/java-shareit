package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestItemRequestServiceImp {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        itemRequestService = new ItemRequestServiceImp(itemRequestRepository, userRepository, itemRepository);
        user = createUser("Ken", "eken@mail.ts");
        user.setId(1L);
        itemRequest = createItemRequest();
        itemRequestDto = createItemRequestDto();
    }

    @Test
    void addItemRequest_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        //when
        ItemRequestDto addedRequest = itemRequestService.addItemRequest(user.getId(), itemRequestDto);
        //then
        assertThat(addedRequest)
                .isNotNull()
                .isInstanceOf(ItemRequestDto.class)
                .isEqualTo(itemRequestDto);
    }

    @Test
    void addItemRequest_failure_userDoesNotExist() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addItemRequest(wrongId, itemRequestDto));
    }

    @Test
    void updateItemRequest_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        //when
        itemRequestDto.setAvailable(Boolean.TRUE);
        ItemRequestDto updatedItemRequest = itemRequestService.updateItemRequest(itemRequest.getId(), itemRequestDto, user.getId());
        //then
        assertThat(updatedItemRequest)
                .isNotNull()
                .isInstanceOf(ItemRequestDto.class)
                .isEqualTo(itemRequestDto);
    }

    @Test
    void updateItemRequest_failure_noRequestToUpdate() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.updateItemRequest(wrongId, itemRequestDto, user.getId()));
    }

    @Test
    void updateItemRequest_failure_userIsNotAnAuthor() {
        //given
        Long ownerId = 2L;
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //then
        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.updateItemRequest(itemRequest.getId(), itemRequestDto, ownerId));
    }

    @Test
    void getItemRequest_success() {
        //given
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        ItemRequestDto returnedItemRequestDto = itemRequestService.getItemRequest(itemRequest.getId(), user.getId());
        //then
        assertThat(returnedItemRequestDto)
                .isNotNull()
                .isInstanceOf(ItemRequestDto.class)
                .isEqualTo(itemRequestDto);
    }

    @Test
    void getItemRequest_failure_userNotFound() {
        //when
        Long wrongId = -999L;
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(wrongId, user.getId()));
    }

    @Test
    void getItemRequest_failure_userItemRequest() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(wrongId, user.getId()));
    }

    @Test
    void getUserItemRequests_success() {
        //given
        String itemDescription = "very thing";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        //when
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(user.getId());
        //then
        assertThat(itemRequestDtos)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(itemRequestDtos.get(0).getDescription())
                .isNotNull()
                .isEqualTo(itemDescription);
    }

    @Test
    void getUserItemRequests_success_noRequests() {
        //given
        Long ownerId = 2L;
        User owner = createUser("Peter", "iown@mail.ts");
        owner.setId(ownerId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of());
        //when
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(ownerId);
        //then
        assertThat(itemRequestDtos)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void getAllItemRequests_success() {
        //given
        String itemDescription = "very thing";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(user.getId(), page)).thenReturn(new PageImpl<>(List.of(itemRequest)));
        //when
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequests(user.getId(), defaultFrom, defaultSize);
        //then
        assertThat(itemRequestDtos)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(itemRequestDtos.get(0).getDescription())
                .isNotNull()
                .isEqualTo(itemDescription);
    }

    @Test
    void getAllItemRequests_failure_notUser() {
        //when
        Long wrongId = -999L;
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllItemRequests(wrongId, defaultFrom,
                defaultSize));
    }

    @Test
    void deleteItemRequest_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        doNothing().when(itemRequestRepository).delete(any(ItemRequest.class));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), any())).thenReturn(Page.empty());
        //when
        itemRequestService.deleteItemRequest(user.getId(), itemRequest.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(user.getId(), defaultFrom, defaultSize);
        //then
        assertThat(requests)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void deleteItemRequest_failure_notUser() {
        //when
        Long wrongId = -999L;
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(wrongId, itemRequest.getId()));
    }

    @Test
    void deleteItemRequest_failure_noItemRequest() {
        //when
        Long wrongItemId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(user.getId(), wrongItemId));
    }

    @Test
    void deleteItemRequest_failure_userIsNotAuthor() {
        //given
        Long wrongId = 999L;
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(wrongId, itemRequest.getId()));
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private ItemRequest createItemRequest() {
        return ItemRequest.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .requestor(user)
                .created(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .available(Boolean.FALSE)
                .build();
    }

    private ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.FALSE)
                .created(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .items(List.of())
                .build();
    }
}
