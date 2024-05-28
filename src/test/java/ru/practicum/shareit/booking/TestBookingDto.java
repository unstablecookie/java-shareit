package ru.practicum.shareit.booking;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.booking.dto.BookingMinDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TestBookingDto {
    LocalDateTime start = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
    LocalDateTime end = LocalDateTime.of(2025, 1, 1, 2, 1, 1);

    @Autowired
    private JacksonTester<BookingDto> bookingDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingFullDto> bookingFullDtoJacksonTester;
    @Autowired
    private JacksonTester<BookingMinDto> bookingMinDtoJacksonTester;

    @Test
    void bookingDtoJacksonTester_success() throws IOException {
        //given
        BookingDto bookingDto = createBookingDto();
        //when
        JsonContent<BookingDto> content = bookingDtoJacksonTester.write(bookingDto);
        //then
        assertThat(content)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(content)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());
        assertThat(content)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());
        assertThat(content)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDto.getBookerId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().name());
    }

    @Test
    void bookingFullDtoJacksonTester_success() throws IOException {
        //given
        BookingFullDto bookingFullDto = createBookingFullDto();
        //when
        JsonContent<BookingFullDto> content = bookingFullDtoJacksonTester.write(bookingFullDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingFullDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingFullDto.getStart().toString());
        assertThat(content)
                .extractingJsonPathMapValue("$.item")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingFullDto.getItem().getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingFullDto.getItem().getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingFullDto.getItem().getDescription());
        assertThat(content)
                .extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingFullDto.getItem().getAvailable());
        assertThat(content)
                .extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(bookingFullDto.getItem().getRequestId().intValue());
        assertThat(content)
                .extractingJsonPathMapValue("$.booker")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingFullDto.getBooker().getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingFullDto.getBooker().getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingFullDto.getBooker().getEmail());
        assertThat(content)
                .extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingFullDto.getStatus().name());
    }

    @Test
    void bookingMinDtoJacksonTester_success() throws IOException {
        //given
        BookingMinDto bookingMinDto = createBookingMinDto();
        //when
        JsonContent<BookingMinDto> content = bookingMinDtoJacksonTester.write(bookingMinDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingMinDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingMinDto.getBookerId().intValue());
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Ken")
                .email("eken@mail.ts")
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(1L)
                .status(Status.WAITING)
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

    private BookingMinDto createBookingMinDto() {
        return BookingMinDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();
    }
}
