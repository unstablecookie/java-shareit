package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.TimeOverlapException;
import ru.practicum.shareit.error.UnsupportedStatusException;
import ru.practicum.shareit.error.UserMissMatchException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestBookingServiceImp {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    private BookingService bookingService;
    private BookingDto bookingDto;
    private BookingFullDto bookingFullDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private User user;
    private User owner;
    private Item item;
    private Booking firstBooking;
    private Long userId = 1L;
    private Long itemId = 1L;
    private Long ownerId = 2L;
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private int defaultFrom = 0;
    private int defaultSize = 10;

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
        bookingService = new BookingServiceImp(itemRepository, userRepository, bookingRepository);
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
        firstBooking = BookingMapper.toBooking(bookingDto, item, user);
    }

    @Test
    void addBooking_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        firstBooking.setId(1L);
        //when
        BookingFullDto bookingFullDtoResponce = bookingService.addBooking(bookingDto, userId);
        //then
        assertThat(bookingFullDto)
                .isNotNull()
                .isInstanceOf(BookingFullDto.class)
                .isEqualTo(bookingFullDtoResponce);
    }

    @Test
    void addBooking_failure_nonExistingUser() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, wrongId));
    }

    @Test
    void addBooking_failure_startAfterEnd() {
        //given
        LocalDateTime startAfterEnd = LocalDateTime.of(2026, 1, 1, 1, 1, 1);
        //when
        bookingDto.setStart(startAfterEnd);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //then
        assertThrows(ResponseStatusException.class, () -> bookingService.addBooking(bookingDto, userId));
    }

    @Test
    void addBooking_failure_noItem() {
        //given
        Long nonExistingItemId = 999L;
        //when
        bookingDto.setItemId(nonExistingItemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, userId));
    }

    @Test
    void addBooking_failure_ownerIsRequestor() {
        //when
        bookingDto.setBookerId(ownerId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, ownerId));
    }

    @Test
    void addBooking_failure_itemNotAvailable() {
        //given
        Boolean itemNotAvailable = Boolean.FALSE;
        //when
        item.setAvailable(itemNotAvailable);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //then
        assertThrows(ResponseStatusException.class, () -> bookingService.addBooking(bookingDto, userId));
    }

    @Test
    void addBooking_failure_nonExistingItem() {
        //when
        Long wrongId = -999L;
        bookingDto.setItemId(wrongId);
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, wrongId));
    }

    @Test
    void addBooking_failure_withTimeIntersetion() {
        //given
        Booking intersetionBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2025, 1, 1, 0, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 1, 2, 1))
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //when
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(List.of(intersetionBooking));
        //then
        assertThrows(TimeOverlapException.class, () -> bookingService.addBooking(bookingDto, userId));
    }

    @Test
    void addBooking_failure_userIsAnOwner() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //when
        item.setOwner(userId);
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, userId));
    }

    @Test
    void getBooking_success() {
        //given
        Long bookingId = 1L;
        firstBooking.setId(bookingId);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(firstBooking));
        //when
        BookingFullDto returnedBookingFullDto = bookingService.getBooking(bookingId, userId);
        //then
        assertThat(returnedBookingFullDto)
                .isNotNull()
                .isInstanceOf(BookingFullDto.class)
                .isEqualTo(bookingFullDto);
    }

    @Test
    void getBooking_failure_wrongBookingId() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(wrongId, userId));
    }

    @Test
    void getBooking_failure_wrongUserId() {
        //given
        Long bookingId = 1L;
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, wrongId));
    }

    @Test
    void getBooking_failure_requestorIsNotBooker() {
        //given
        Long bookingId = 1L;
        //when
        Long notBookerId = 3L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, notBookerId));
    }

    @Test
    void getOwnerBookings_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(ownerId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(ownerId, defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void getOwnerBookings_failure_userNotFound() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getOwnerBookings(wrongId, defaultFrom, defaultSize));
    }

    @Test
    void getOwnerBookingsWithState_success_WAITING() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookingsAndStatus(ownerId, Status.WAITING, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookingsWithState(ownerId, State.WAITING,
                defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getOwnerBookingsWithState_success_ALL() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookings(ownerId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookingsWithState(ownerId, State.ALL,
                defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getOwnerBookingsWithState_success_PAST() {
        //given
        firstBooking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookingsWithState(ownerId, State.PAST,
                defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.APPROVED);
    }

    @Test
    void getOwnerBookingsWithState_success_FUTURE() {
        //given
        firstBooking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookingsWithState(ownerId, State.FUTURE,
                defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.APPROVED);
    }

    @Test
    void getOwnerBookingsWithState_success_CURRENT() {
        //given
        firstBooking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndTimeCurrent(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookingsWithState(ownerId, State.CURRENT,
                defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.APPROVED);
    }

    @Test
    void getOwnerBookingsWithState_success_REJECTED() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        //when
        firstBooking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(ownerId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(ownerId, defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.REJECTED);
    }

    @Test
    void getUserBookings_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByUserIdOrderByStartDesc(userId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookings(userId, defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void getUserBookings_failure_userNotFound() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getUserBookings(wrongId, defaultFrom, defaultSize));
    }

    @Test
    void getUserBookingsWithState_success_WAITING() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(userId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(userId, defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getUserBookingsWithState_success_REJECTED() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        //when
        firstBooking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(userId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(userId, defaultFrom, defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.REJECTED);
    }

    @Test
    void getUserBookingsWithState_success_ALL() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByUserId(userId, page)).thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(userId, State.ALL, defaultFrom,
                defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getUserBookingsWithState_success_PAST() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByUserIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(userId, State.PAST, defaultFrom,
                defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getUserBookingsWithState_success_FUTURE() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByUserIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(userId, State.FUTURE, defaultFrom,
                defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void getUserBookingsWithState_success_CURRENT() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByUserIdAndTimeCurrent(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(firstBooking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(userId, State.CURRENT, defaultFrom,
                defaultSize);
        //then
        assertThat(ownerBookings)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(ownerBookings.get(0).getStatus())
                .isEqualTo(Status.WAITING);
    }

    @Test
    void updateBooking_success() {
        //given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        //when
        Boolean approved = Boolean.TRUE;
        firstBooking.setId(1L);
        BookingFullDto approvedBooking = bookingService.updateBooking(ownerId, firstBooking.getId(), approved);
        bookingFullDto.setStatus(Status.APPROVED);
        //then
        assertThat(approvedBooking)
                .isNotNull()
                .isEqualTo(bookingFullDto);
    }

    @Test
    void updateBooking_failure_userIsNotOwner() {
        //given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        Boolean approved = Boolean.TRUE;
        firstBooking.setId(1L);
        //then
        assertThrows(UserMissMatchException.class, () -> bookingService.updateBooking(userId, firstBooking.getId(),
                approved));
    }

    @Test
    void updateBooking_failure_wrongCurrentStatus() {
        //given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        //when
        Boolean approved = Boolean.TRUE;
        firstBooking.setId(1L);
        firstBooking.setStatus(Status.APPROVED);
        //then
        assertThrows(UnsupportedStatusException.class, () -> bookingService.updateBooking(ownerId, firstBooking.getId(),
                approved));
    }

    @Test
    void cancelBooking_success() {
        //given
        firstBooking.setId(1L);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        //when
        bookingService.cancelBooking(userId, firstBooking.getId());
        //then
        assertThat(firstBooking.getStatus())
                .isNotNull()
                .isEqualTo(Status.CANCELED);
    }

    @Test
    void cancelBooking_failure_wrongUser() {
        //given
        firstBooking.setId(1L);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        //when
        bookingService.cancelBooking(userId, firstBooking.getId());
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBooking(ownerId, firstBooking.getId()));
    }

    @Test
    void deleteBooking_success() {
        //when
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        doNothing().when(bookingRepository).delete(any());
        //then
        bookingService.deleteBooking(itemId);
    }

    @Test
    void deleteBooking_failure_wrongId() {
        //given
        Long wrongId = -999L;
        //when
        when(bookingRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(itemId));
    }
}
