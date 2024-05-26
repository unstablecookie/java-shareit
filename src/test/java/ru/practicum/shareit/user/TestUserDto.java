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
    private Long userId = 1L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private UserDto userDto = userDto = UserDto.builder()
            .id(userId)
            .name(userName)
            .email(userEmail)
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
