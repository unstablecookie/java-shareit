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
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
    private ItemDto itemDto;
    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        itemService = new ItemServiceImp(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        itemDto = createItemDto();
        item = createItem();
        item.setId(1L);
        user = createUser("Ken", "eken@mail.ts");
        user.setId(1L);
        owner = createUser("Peter", "iown@mail.ts");
        owner.setId(2L);
        booking = BookingMapper.toBooking(createBookingDto(), item, user);
        booking.setId(1L);
    }

    @Test
    void addItem_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        //when
        ItemDto addedItemDto = itemService.addItem(owner.getId(), itemDto);
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
        ItemDto updatedItemDto = itemService.updateItem(owner.getId(), itemDto, item.getId());
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
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(user.getId(), itemDto, item.getId()));
    }

    @Test
    void getItem_success_byOwner() {
        //given
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
        //when
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        itemWithBookingsDto.setNextBooking(BookingMapper.toMinBookingDto(booking));
        itemWithBookingsDto.setComments(List.of());
        ItemWithBookingsDto ownersItemWithBookingsDto = itemService.getItem(owner.getId(), item.getId());
        //then
        assertThat(ownersItemWithBookingsDto)
                .isNotNull()
                .isInstanceOf(ItemWithBookingsDto.class)
                .isEqualTo(itemWithBookingsDto);
    }

    @Test
    void getItem_success_byUser() {
        //given
        Comment comment = Comment.builder()
                .id(1L)
                .item(item)
                .text("user comment")
                .author(user)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1))
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        //when
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
        itemWithBookingsDto.setComments(List.of(CommentMapper.toCommentDtoFull(comment, user.getName())));
        ItemWithBookingsDto usersItemWithBookingsDto = itemService.getItem(user.getId(), item.getId());
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
        when(itemRepository.findByOwnerOrderById(owner.getId(), page)).thenReturn(new PageImpl<>(List.of(item)));
        //when
        List<ItemWithBookingsDto> items = itemService.getUserItems(owner.getId(), defaultFrom, defaultSize);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(items.get(0).getName())
                .isNotNull()
                .isEqualTo(item.getName());
    }

    @Test
    void getUserItems_success_noItems() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(itemRepository.findByOwnerOrderById(user.getId(), page)).thenReturn(new PageImpl<>(List.of()));
        //when
        List<ItemWithBookingsDto> items = itemService.getUserItems(user.getId(), defaultFrom, defaultSize);
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
                .isEqualTo(item.getName());
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
        itemService.deleteItem(item.getId());
    }

    @Test
    void deleteItem_failure_noItem() {
        //when
        Long wrongId = -999L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        //then
        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(anyLong()));
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .itemId(1L)
                .bookerId(1L)
                .build();
    }
}
