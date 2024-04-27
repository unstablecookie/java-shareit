package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.error.EntityAlreadyExistException;
import ru.practicum.shareit.user.error.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> storage = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    private Long counter = 1L;

    @Override
    public User getUser(Long id) {
        User user = storage.get(id);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, String.format("user id: %d ,do not exist", id)
            );
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return storage.values().stream().collect(Collectors.toList());
    }

    @Override
    public User addUser(Long userId, User user) throws EntityAlreadyExistException {
        validateEmail(user.getEmail());
        if (userId != null) {
            if (storage.get(userId) != null) {
                throw new EntityAlreadyExistException(String.format("User with id %d already exists", userId));
            }
            user.setId(userId);
            updateStorage(userId, user);
            return user;
        } else {
            user.setId(counter);
            counter++;
            updateStorage(user.getId(), user);
            return user;
        }
    }

    @Override
    public User updateUser(Long id, User user) throws EntityAlreadyExistException {
        User oldUser = storage.get(id);
        if (oldUser == null) {
            throw new EntityNotFoundException(String.format("user %d do not exists", id));
        }
        User updatedUser = UserMapper.updateUserWithUser(oldUser, user);
        if (oldUser.getEmail() != updatedUser.getEmail()) {
            emails.remove(oldUser.getEmail());
            validateEmail(updatedUser.getEmail());
            emails.add(updatedUser.getEmail());
        }
        updateStorage(id, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        User oldUser = storage.get(id);
        emails.remove(oldUser.getEmail());
        storage.remove(id);
    }

    private void updateStorage(Long id, User user) {
        storage.put(id, user);
        emails.add(user.getEmail());
    }

    private void validateEmail(String email) throws EntityAlreadyExistException {
        if (emails.contains(email)) {
            throw new EntityAlreadyExistException(String.format("User with email %s already exists", email));
        }
    }
}
