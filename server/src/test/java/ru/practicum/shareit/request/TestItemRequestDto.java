package ru.practicum.shareit.request;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TestItemRequestDto {
    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;

    @Test
    void itemRequestDtoJacksonTester_success() throws IOException {
        //given
        ItemRequestDto itemRequestDto = createItemRequestDto();
        //when
        JsonContent<ItemRequestDto> content = itemRequestDtoJacksonTester.write(itemRequestDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemRequestDto.getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(content)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemRequestDto.getAvailable());
        assertThat(content)
                .extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(content)
                .extractingJsonPathArrayValue("$.items")
                .isNotNull();
        assertThat(content)
                .extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestDto.getItems().get(0).getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestDto.getItems().get(0).getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestDto.getItems().get(0).getDescription());
        assertThat(content)
                .extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestDto.getItems().get(0).getAvailable());
        assertThat(content)
                .extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestDto.getItems().get(0).getRequestId().intValue());
    }

    private ItemRequestDto createItemRequestDto() {
        return ItemRequestDto.builder()
            .id(1L)
            .name("thing name")
            .description("very thing")
            .available(Boolean.TRUE)
            .created(LocalDateTime.of(2025, 1, 1, 1, 1, 1))
            .items(List.of(createItemDto()))
            .build();
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
}
