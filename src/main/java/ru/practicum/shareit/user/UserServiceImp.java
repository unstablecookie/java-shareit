package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityAlreadyExistException;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException(String.format("user id: %d ,do not exist", id));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.getUsers();
        return UserMapper.toUsersDto(users);
    }

    @Override
    public UserDto addUser(Long userId, UserDto userDto) throws EntityAlreadyExistException {
        validateEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        Optional<User> createdUser = userRepository.addUser(userId, user);
        if (createdUser.isEmpty()) {
            throw new EntityAlreadyExistException(String.format("user with id %d already exists", userId));
        }
        return UserMapper.toUserDto(createdUser.get());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) throws EntityAlreadyExistException {
        User oldUser = userRepository.getUser(id);
        if (oldUser == null) {
            throw new EntityNotFoundException(String.format("user %d do not exists", id));
        }
        User user = UserMapper.toUser(userDto);
        User updatedUser = UserMapper.updateUserWithUser(oldUser, user);
        if (!oldUser.getEmail().equals(updatedUser.getEmail())) {
            validateEmail(updatedUser.getEmail());
        }
        userRepository.updateUser(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private void validateEmail(String email) throws EntityAlreadyExistException {
        userRepository.getUsers().stream()
                .forEach(x -> {
                    if (x.getEmail().equals(email)) {
                        throw new EntityAlreadyExistException(String.format("User with email %s already exists", email));
                    }
                });
    }
}
