package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
public class TestUserController {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final String headerXSharerUserId = "X-Sharer-User-Id";
    private User user;
    private UserDto userDto;
    private Long userId = 1L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";

    @BeforeEach
    private void init() {
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void getUser_success() throws Exception {
        //when
        when(userService.getUser(anyLong())).thenReturn(userDto);
        //given
        mvc.perform(get("/users/{id}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(userName)))
                .andExpect(jsonPath("$.email", is(userEmail)));
    }

    @Test
    void getUsers_success() throws Exception {
        //when
        when(userService.getUsers()).thenReturn(List.of(userDto));
        //given
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userId), Long.class))
                .andExpect(jsonPath("$[0].name", is(userName)))
                .andExpect(jsonPath("$[0].email", is(userEmail)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addUser_success() throws Exception {
        //when
        when(userService.addUser(anyLong(), any())).thenReturn(userDto);
        //given
        mvc.perform(post("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(userName)))
                .andExpect(jsonPath("$.email", is(userEmail)));
    }

    @Test
    void updateUser_success() throws Exception {
        //when
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);
        //given
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(userName)))
                .andExpect(jsonPath("$.email", is(userEmail)));
    }

    @Test
    void deleteUser_success() throws Exception {
        //given
        mvc.perform(delete("/users/{userId}", userId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
