package ru.practicum.shareit.user;

import ru.practicum.shareit.user.error.EntityAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUser(Long id);

    List<User> getUsers();

    User addUser(Long userId, User user) throws EntityAlreadyExistException;

    User updateUser(Long id, User user) throws EntityAlreadyExistException;

    void deleteUser(Long id);
}
