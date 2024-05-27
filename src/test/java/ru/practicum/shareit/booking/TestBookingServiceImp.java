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
    private User user;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        bookingService = new BookingServiceImp(itemRepository, userRepository, bookingRepository);
        user = createUser("Ken", "eken@mail.ts");
        user.setId(1L);
        item = createItem();
        bookingDto = createBookingDto();
        booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setId(1L);
    }

    @Test
    void addBooking_success() {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        //when
        BookingFullDto bookingFullDtoResponce = bookingService.addBooking(bookingDto, user.getId());
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
        assertThrows(ResponseStatusException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
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
        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void addBooking_failure_ownerIsRequestor() {
        //given
        Long ownerId = 2L;
        //when
        bookingDto.setBookerId(ownerId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //then
        assertThrows(UserMissMatchException.class, () -> bookingService.addBooking(bookingDto, ownerId));
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
        assertThrows(ResponseStatusException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
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
        assertThrows(TimeOverlapException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void addBooking_failure_userIsAnOwner() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        //when
        item.setOwner(user.getId());
        //then
        assertThrows(UserMissMatchException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
    }

    @Test
    void getBooking_success() {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        //when
        BookingFullDto returnedBookingFullDto = bookingService.getBooking(booking.getId(), user.getId());
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
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(wrongId, user.getId()));
    }

    @Test
    void getBooking_failure_wrongUserId() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(booking.getId(), wrongId));
    }

    @Test
    void getBooking_failure_requestorIsNotBooker() {
        //when
        Long notBookerId = 3L;
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(booking.getId(), notBookerId));
    }

    @Test
    void getOwnerBookings_success() {
        //given
        Long ownerId = 2L;
        BookingDto bookingDto = createBookingDto();
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(ownerId, page)).thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookingsAndStatus(ownerId, Status.WAITING, page))
                .thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findAllOwnerBookings(ownerId, page)).thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        booking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        booking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        booking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        when(bookingRepository.findByOwnerIdAndTimeCurrent(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
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
        Long ownerId = 2L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        PageRequest page = PageRequest.of(defaultFrom > 0 ? defaultFrom / defaultSize : 0, defaultSize);
        //when
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(ownerId, page))
                .thenReturn(new PageImpl<>(List.of(booking)));
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
        when(bookingRepository.findByUserIdOrderByStartDesc(user.getId(), page)).thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookings(user.getId(), defaultFrom, defaultSize);
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
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(user.getId(), page)).thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(user.getId(), defaultFrom, defaultSize);
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
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findAllOwnerBookingsOrderByStartDesc(user.getId(), page)).thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingFullDto> ownerBookings = bookingService.getOwnerBookings(user.getId(), defaultFrom, defaultSize);
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
        when(bookingRepository.findByUserId(user.getId(), page)).thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(user.getId(), State.ALL, defaultFrom,
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
                .thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(user.getId(), State.PAST, defaultFrom,
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
                .thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(user.getId(), State.FUTURE, defaultFrom,
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
                .thenReturn(new PageImpl<>(List.of(booking)));
        //when
        List<BookingFullDto> ownerBookings = bookingService.getUserBookingsWithState(user.getId(), State.CURRENT, defaultFrom,
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
        Long ownerId = 2L;
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser("Peter", "iown@mail.ts")));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        //when
        Boolean approved = Boolean.TRUE;
        BookingFullDto approvedBooking = bookingService.updateBooking(ownerId, booking.getId(), approved);
        bookingFullDto.setStatus(Status.APPROVED);
        //then
        assertThat(approvedBooking)
                .isNotNull()
                .isEqualTo(bookingFullDto);
    }

    @Test
    void updateBooking_failure_userIsNotOwner() {
        //given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        Boolean approved = Boolean.TRUE;
        booking.setId(1L);
        //then
        assertThrows(UserMissMatchException.class, () -> bookingService.updateBooking(user.getId(), booking.getId(),
                approved));
    }

    @Test
    void updateBooking_failure_wrongCurrentStatus() {
        //given
        Long ownerId = 2L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser("Peter", "iown@mail.ts")));
        //when
        Boolean approved = Boolean.TRUE;
        booking.setStatus(Status.APPROVED);
        //then
        assertThrows(UnsupportedStatusException.class, () -> bookingService.updateBooking(ownerId, booking.getId(),
                approved));
    }

    @Test
    void cancelBooking_success() {
        //given
        booking.setId(1L);
        user.setId(1L);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        //when
        bookingService.cancelBooking(user.getId(), booking.getId());
        //then
        assertThat(booking.getStatus())
                .isNotNull()
                .isEqualTo(Status.CANCELED);
    }

    @Test
    void cancelBooking_failure_wrongUser() {
        //given
        Long ownerId = 2L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        //when
        bookingService.cancelBooking(user.getId(), booking.getId());
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBooking(ownerId, booking.getId()));
    }

    @Test
    void deleteBooking_success() {
        //given
        user.setId(1L);
        //when
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).delete(any());
        //then
        bookingService.deleteBooking(item.getId());
    }

    @Test
    void deleteBooking_failure_wrongId() {
        //given
        Long wrongId = -999L;
        //when
        when(bookingRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(wrongId));
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Ken")
                .email("eken@mail.ts")
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

    private BookingFullDto createBookingFullDto() {
        return BookingFullDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 1, 1, 2, 1, 1))
                .item(createItemDto())
                .booker(createUserDto())
                .status(Status.WAITING)
                .build();
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
    }
}
