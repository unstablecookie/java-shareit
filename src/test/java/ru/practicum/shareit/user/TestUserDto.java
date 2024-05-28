package ru.practicum.shareit.user;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TestUserDto {
    private UserDto userDto = userDto = UserDto.builder()
            .id(1L)
            .name("Ken")
            .email("eken@mail.ts")
            .build();

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void setUserDtoJacksonTester_success() throws IOException {
        //when
        JsonContent<UserDto> content = userDtoJacksonTester.write(userDto);
        //then
        assertThat(content)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId().intValue());
        assertThat(content)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(content)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}
