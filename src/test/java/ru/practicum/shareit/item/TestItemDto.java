package ru.practicum.shareit.item;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingMinDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TestItemDto {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;
    @Autowired
    private JacksonTester<ItemWithBookingsDto> itemWithBookingsDtoJacksonTester;

    @Test
    void itemDtoJacksonTester_success() throws IOException {
        //given
        ItemDto itemDto = createItemDto();
        itemDto.setRequestId(1L);
        //when
        JsonContent<ItemDto> content = itemDtoJacksonTester.write(itemDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(content)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(content)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
    }

    @Test
    void itemWithBookingsDtoJacksonTester_success() throws IOException {
        //when
        ItemWithBookingsDto itemWithBookingsDto = ItemWithBookingsDto.builder()
            .id(1L)
            .name("thing")
            .description("very thing")
            .available(Boolean.TRUE)
            .requestId(1L)
            .lastBooking(createBookingMinDto())
            .nextBooking(createBookingMinDto())
            .comments(List.of(createCommentDtoFull()))
            .build();
        JsonContent<ItemWithBookingsDto> content = itemWithBookingsDtoJacksonTester.write(itemWithBookingsDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemWithBookingsDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemWithBookingsDto.getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemWithBookingsDto.getDescription());
        assertThat(content)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemWithBookingsDto.getAvailable());
        assertThat(content)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemWithBookingsDto.getRequestId().intValue());
        assertThat(content)
                .extractingJsonPathMapValue("$.lastBooking")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathMapValue("$.nextBooking")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemWithBookingsDto.getLastBooking().getId().intValue());
        assertThat(content)
                .extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemWithBookingsDto.getNextBooking().getId().intValue());
        assertThat(content)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemWithBookingsDto.getLastBooking().getId().intValue());
        assertThat(content)
                .extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemWithBookingsDto.getNextBooking().getId().intValue());
        assertThat(content)
                .extractingJsonPathArrayValue("$.comments")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemWithBookingsDto.getComments().get(0).getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemWithBookingsDto.getComments().get(0).getText());
        assertThat(content)
                .extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemWithBookingsDto.getComments().get(0).getAuthorName());
        assertThat(content)
                .extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemWithBookingsDto.getComments().get(0).getCreated().toString());
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .build();
    }

    private BookingMinDto createBookingMinDto() {
        return BookingMinDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();
    }

    private CommentDtoFull createCommentDtoFull() {
        return CommentDtoFull.builder()
                .id(1L)
                .text("it's good")
                .authorName("Ken")
                .created(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
                .build();
    }
}
