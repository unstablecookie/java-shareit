package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
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
public class TestItemServiceImp {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private BookingDto bookingDto;
    private BookingFullDto bookingFullDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private User user;
    private User owner;
    private Item item;
    private Comment comment;
    private Booking firstBooking;
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
        itemService = new ItemServiceImp(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
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
        firstBooking = BookingMapper.toBooking(bookingDto, item, user);
    }

    @Test
    void addItem_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        //when
        ItemDto addedItemDto = itemService.addItem(ownerId, itemDto);
        //then
        assertThat(addedItemDto)
                .isNotNull()
                .isInstanceOf(ItemDto.class)
                .isEqualTo(itemDto);
    }

    @Test
    void addItem_failure_userDoesNotExist() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(wrongId, itemDto));
    }

    @Test
    void updateItem_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        //when
        itemDto.setAvailable(Boolean.FALSE);
        ItemDto updatedItemDto = itemService.updateItem(ownerId, itemDto, itemId);
        //then
        assertThat(updatedItemDto)
                .isNotNull()
                .isInstanceOf(ItemDto.class)
                .isEqualTo(itemDto);
    }

    @Test
    void updateItem_failure_userIsNotAnOwner() {
        //given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //then
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(userId, itemDto, itemId));
    }

    @Test
    void getItem_success_byOwner() {
        //given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of(firstBooking));
        //when
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        itemWithBookingsDto.setNextBooking(BookingMapper.toMinBookingDto(firstBooking));
        itemWithBookingsDto.setComments(List.of());
        ItemWithBookingsDto ownersItemWithBookingsDto = itemService.getItem(ownerId, itemId);
        //then
        assertThat(ownersItemWithBookingsDto)
                .isNotNull()
                .isInstanceOf(ItemWithBookingsDto.class)
                .isEqualTo(itemWithBookingsDto);
    }

    @Test
    void getItem_success_byUser() {
        //given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        //when
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        itemWithBookingsDto.setComments(List.of(CommentMapper.toCommentDtoFull(comment, userName)));
        ItemWithBookingsDto usersItemWithBookingsDto = itemService.getItem(userId, itemId);
        //then
        assertThat(usersItemWithBookingsDto)
                .isNotNull()
                .isInstanceOf(ItemWithBookingsDto.class)
                .isEqualTo(itemWithBookingsDto);
    }

    @Test
    void getUserItems_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRepository.findByOwnerOrderById(ownerId, page)).thenReturn(new PageImpl<>(List.of(item)));
        //when
        List<ItemWithBookingsDto> items = itemService.getUserItems(ownerId, defaultFrom, defaultSize);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(items.get(0).getName())
                .isNotNull()
                .isEqualTo(itemName);
    }

    @Test
    void getUserItems_success_noItems() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRepository.findByOwnerOrderById(userId, page)).thenReturn(new PageImpl<>(List.of()));
        //when
        List<ItemWithBookingsDto> items = itemService.getUserItems(userId, defaultFrom, defaultSize);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void searchItem_success() {
        //given
        String searchText = "thing";
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRepository.findByDescriptionContainingIgnoreCase(searchText, page)).thenReturn(List.of(item));
        //when
        List<ItemDto> items = itemService.searchItem(searchText, defaultFrom, defaultSize);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(items.get(0).getName())
                .isNotNull()
                .isEqualTo(itemName);
    }

    @Test
    void searchItem_success_noText() {
        //given
        String searchText = "";
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        //when
        List<ItemDto> items = itemService.searchItem(searchText, defaultFrom, defaultSize);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void deleteItem_success() {
        //when
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of());
        doNothing().when(bookingRepository).deleteAllInBatch(any());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(any());
        //then
        itemService.deleteItem(itemId);
    }

    @Test
    void deleteItem_failure_noItem() {
        //when
        Long wrongId = -999L;
        when(itemRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(anyLong()));
    }
}
