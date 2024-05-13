package ru.practicum.shareit.user;

import ru.practicum.shareit.error.EntityAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> getUser(Long id);

    List<UserDto> getUsers();

    UserDto addUser(Long userId, UserDto user) throws EntityAlreadyExistException;

    UserDto updateUser(Long id, UserDto user) throws EntityAlreadyExistException;

    void deleteUser(Long id);
}
