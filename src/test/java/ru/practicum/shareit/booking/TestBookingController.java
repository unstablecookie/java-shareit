package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.TimeOverlapException;
import ru.practicum.shareit.error.UnsupportedStatusException;
import ru.practicum.shareit.error.UserMissMatchException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = BookingController.class)
public class TestBookingController {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private Long bookingId = 1L;
    private final String headerXSharerUserId = "X-Sharer-User-Id";
    LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
    LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);

    @Test
    void addBooking_success() throws Exception {
        //given
        BookingDto bookingDto = createBookingDto();
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.addBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingFullDto);
        //then
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.start", is((start).toString())))
                .andExpect(jsonPath("$.end", is((end).toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void addBooking_failure_timeOverlap() throws Exception {
        //given
        BookingDto bookingDto = createBookingDto();
        when(bookingService.addBooking(any(BookingDto.class), anyLong())).thenThrow(TimeOverlapException.class);
        //then
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooking_failure_wrongBookingId() throws Exception {
        //given
        when(bookingService.getBooking(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);
        //then
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooking_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingFullDto);
        //then
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.start", is((start).toString())))
                .andExpect(jsonPath("$.end", is((end).toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void getBookingWithOwner_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.getOwnerBookings(anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookingFullDto));
        //then
        mvc.perform(get("/bookings/owner")
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
                .andExpect(jsonPath("$[0].start", is((start).toString())))
                .andExpect(jsonPath("$[0].end", is((end).toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getBookingWithOwnerWithState_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.getOwnerBookingsWithState(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));
        //then
        mvc.perform(get("/bookings/owner")
                        .header(headerXSharerUserId, 1)
                        .param("state", State.ALL.toString())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
                .andExpect(jsonPath("$[0].start", is((start).toString())))
                .andExpect(jsonPath("$[0].end", is((end).toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUserBookings_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.getUserBookings(anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookingFullDto));
        //then
        mvc.perform(get("/bookings")
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
                .andExpect(jsonPath("$[0].start", is((start).toString())))
                .andExpect(jsonPath("$[0].end", is((end).toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUserBookingsWithState_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.getUserBookingsWithState(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingFullDto));
        //then
        mvc.perform(get("/bookings")
                        .header(headerXSharerUserId, 1)
                        .param("state", State.ALL.toString())
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingId), Long.class))
                .andExpect(jsonPath("$[0].start", is((start).toString())))
                .andExpect(jsonPath("$[0].end", is((end).toString())))
                .andExpect(jsonPath("$[0].item", notNullValue()))
                .andExpect(jsonPath("$[0].booker", notNullValue()))
                .andExpect(jsonPath("$[0].status", is(Status.WAITING.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateBooking_success() throws Exception {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingFullDto);
        //when
        bookingFullDto.setStatus(Status.APPROVED);
        //then
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .param("approved", Boolean.TRUE.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId), Long.class))
                .andExpect(jsonPath("$.start", is((start).toString())))
                .andExpect(jsonPath("$.end", is((end).toString())))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void updateBooking_failure_wrongStatus() throws Exception {
        //given
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(UnsupportedStatusException.class);
        //then
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .param("approved", Boolean.TRUE.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_failure_wrongUser() throws Exception {
        //given
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(UserMissMatchException.class);
        //then
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .param("approved", Boolean.TRUE.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBooking_success() throws Exception {
        //then
        mvc.perform(delete("/bookings/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cancelBooking_success() throws Exception {
        //then
        mvc.perform(put("/bookings/cancel/{bookingId}", bookingId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(1L)
                .build();
    }

    private BookingFullDto createBookingFullDto() {
        return BookingFullDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(createItemDto())
                .booker(createUserDto())
                .status(Status.WAITING)
                .build();
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
}
