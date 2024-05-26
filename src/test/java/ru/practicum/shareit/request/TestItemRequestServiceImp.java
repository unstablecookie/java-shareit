package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
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

    private BookingDto bookingDto;
    private BookingFullDto bookingFullDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private User user;
    private User owner;
    private Item item;
    private Comment comment;
    private Booking firstBooking;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Long userId = 1L;
    private Long itemId = 1L;
    private Long ownerId = 2L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private int defaultFrom = 0;
    private int defaultSize = 10;
    private String text = "it's good";
    private Long commentId = 1L;
    private Long itemRequestId = 1L;

    @BeforeEach
    private void init() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .build();
        userDto = UserDto.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        itemRequestService = new ItemRequestServiceImp(itemRequestRepository, userRepository, itemRepository);
        bookingDto = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(1L)
                .build();
        bookingFullDto = BookingFullDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(Status.WAITING)
                .build();
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();
        item = Item.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .item(item)
                .text(text)
                .author(user)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1))
                .build();
        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .name(itemName)
                .description(itemDescription)
                .requestor(user)
                .created(LocalDateTime.of(2024, 1, 1, 0, 1, 1))
                .available(Boolean.FALSE)
                .build();
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, List.of());
        firstBooking = BookingMapper.toBooking(bookingDto, item, user);
    }

    @Test
    void addItemRequest_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        //when
        ItemRequestDto addedRequest = itemRequestService.addItemRequest(userId, itemRequestDto);
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
        ItemRequestDto updatedItemRequest = itemRequestService.updateItemRequest(itemRequestId, itemRequestDto, userId);
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
                itemRequestService.updateItemRequest(wrongId, itemRequestDto, userId));
    }

    @Test
    void updateItemRequest_failure_userIsNotAnAuthor() {
        //given
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //then
        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.updateItemRequest(itemRequestId, itemRequestDto, ownerId));
    }

    @Test
    void getItemRequest_success() {
        //given
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        ItemRequestDto returnedItemRequestDto = itemRequestService.getItemRequest(itemRequestId, userId);
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
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(wrongId, userId));
    }

    @Test
    void getItemRequest_failure_userItemRequest() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequest(wrongId, userId));
    }

    @Test
    void getUserItemRequests_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        //when
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserItemRequests(userId);
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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, page)).thenReturn(new PageImpl<>(List.of(itemRequest)));
        //when
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequests(userId, defaultFrom, defaultSize);
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
        itemRequestService.deleteItemRequest(userId, itemRequestId);
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(userId, defaultFrom, defaultSize);
        //then
        assertThat(requests)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void deleteItemRequest_failure_notUser() {
        //when
        Long wrongUserId = -999L;
        when(userRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(wrongUserId, itemRequestId));
    }

    @Test
    void deleteItemRequest_failure_noItemRequest() {
        //when
        Long wrongItemId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(userId, wrongItemId));
    }

    @Test
    void deleteItemRequest_failure_userIsNotAuthor() {
        //given
        Long wrongUserId = 999L;
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        //then
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.deleteItemRequest(wrongUserId, itemRequestId));
    }
}
