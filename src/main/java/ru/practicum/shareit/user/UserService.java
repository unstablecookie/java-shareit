package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(Long id);

    List<UserDto> getUsers();

    UserDto addUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    void deleteUser(Long id);
}
