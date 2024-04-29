package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User getUser(Long id);

    List<User> getUsers();

    Optional<User> addUser(Long userId, User user);

    void updateUser(User updatedUser);

    void deleteUser(Long id);
}
