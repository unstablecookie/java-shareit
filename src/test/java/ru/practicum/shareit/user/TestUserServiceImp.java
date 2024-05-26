package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserServiceImp {
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;
    private User user;
    private Long userId = 1L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";

    @BeforeEach
    private void init() {
        userDto = UserDto.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        userService = new UserServiceImp(userRepository);
    }

    @Test
    void getUser_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        UserDto returnedUserDto = userService.getUser(userId);
        //then
        assertThat(returnedUserDto)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
    }

    @Test
    void getUser_failure_wrongId() {
        //when
        Long wrongId = -999L;
        //then
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(wrongId));
    }

    @Test
    void getUsers_success() {
        //given
        when(userRepository.findAll()).thenReturn(List.of(user));
        //when
        List<UserDto> users = userService.getUsers();
        //then
        assertThat(users)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(users.get(0).getName())
                .isNotNull()
                .isEqualTo(userName);
    }

    @Test
    void getUsers_success_noUsers() {
        //given
        when(userRepository.findAll()).thenReturn(List.of());
        //when
        List<UserDto> users = userService.getUsers();
        //then
        assertThat(users)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void addUser_success() {
        //given
        when(userRepository.save(any(User.class))).thenReturn(user);
        //when
        UserDto addedUser = userService.addUser(userId, userDto);
        //then
        assertThat(addedUser)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
    }

    @Test
    void updateUser_success() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        String changedUserName = "changed name";
        userDto.setName(changedUserName);
        user.setName(changedUserName);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto changedUserDto = userService.updateUser(userId, userDto);
        //then
        assertThat(changedUserDto)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
    }
}
