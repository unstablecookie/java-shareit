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
    public Optional<UserDto> getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.isEmpty() ? Optional.empty() : Optional.of(UserMapper.toUserDto(user.get()));
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUsersDto(users);
    }

    @Override
    public UserDto addUser(Long userId, UserDto userDto) throws EntityAlreadyExistException {
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) throws EntityAlreadyExistException {
        Optional<User> oldUser = userRepository.findById(id);
        if (oldUser.isEmpty()) {
            throw new EntityNotFoundException(String.format("user %d do not exists", id));
        }
        User user = UserMapper.toUser(userDto);
        User updatedUser = UserMapper.updateUserWithUser(oldUser.get(), user);
        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("user %d do not exists", id));
        }
        userRepository.delete(user.get());
    }
}
