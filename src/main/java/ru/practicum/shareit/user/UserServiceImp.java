package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.error.EntityAlreadyExistException;
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
        User user = userRepository.getUser(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.getUsers();
        return UserMapper.toUsersDto(users);
    }

    @Override
    public UserDto addUser(Long userId, UserDto userDto) throws EntityAlreadyExistException {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.addUser(userId, user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) throws EntityAlreadyExistException {
        User user = UserMapper.toUser(userDto);
        User updatedUser = userRepository.updateUser(id, user);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}
