package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", id)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUsersDto(users);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User oldUser = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", id)));
        User user = UserMapper.toUser(userDto);
        User updatedUser = UserMapper.updateUserWithUser(oldUser, user);
        userRepository.save(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("user id: %d was not found", id)));
        userRepository.delete(user);
    }
}
