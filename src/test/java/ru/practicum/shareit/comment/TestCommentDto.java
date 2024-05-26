package ru.practicum.shareit.comment;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TestCommentDto {
    private Long id = 1L;
    private String text = "user comment";
    private String authorName = "Author Name";
    private LocalDateTime created = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
    private CommentDto commentDto = CommentDto.builder()
            .id(id)
            .text(text)
            .build();
    private CommentDtoFull commentDtoFull = CommentDtoFull.builder()
            .id(id)
            .text(text)
            .authorName(authorName)
            .created(created)
            .build();

    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;
    @Autowired
    private JacksonTester<CommentDtoFull> commentDtoFullJacksonTester;

    @Test
    void commentDtoJacksonTester_success() throws IOException {
        //when
        JsonContent<CommentDto> content = commentDtoJacksonTester.write(commentDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
    }

    @Test
    void commentDtoFullJacksonTester_success() throws IOException {
        //when
        JsonContent<CommentDtoFull> content = commentDtoFullJacksonTester.write(commentDtoFull);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDtoFull.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDtoFull.getText());
        assertThat(content)
                .extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDtoFull.getAuthorName());
        assertThat(content)
                .extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDtoFull.getCreated().toString());
    }
}
