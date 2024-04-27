package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.error.EntityAlreadyExistException;
import ru.practicum.shareit.user.error.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class UserRepositoryTest {
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void init() {
        Long userId = 1L;
        String userName = "username";
        String email = "usermail@mail.ru";
        user = new User(userId, userName, email);
        userRepository = new UserRepositoryInMemory();
    }

    @Test
    void getUser_success() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        Long id = 1L;
        //when
        User retrievedUser = userRepository.getUser(id);
        //then
        assertThat(retrievedUser)
                .isNotNull()
                .isInstanceOf(User.class);
        assertThat(user.getId())
                .isNotNull()
                .isInstanceOf(Long.class)
                .isEqualTo(id);
    }

    @Test
    void getUser_failure_existingUser() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        Long id = -999L;
        //then
        assertThatThrownBy(() ->
                userRepository.getUser(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("user id: -999 ,do not exist");
    }

    @Test
    void getUsers_success() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        Long userId = 2L;
        String userName = "anotherusername";
        String email = "anotherusermail@mail.ru";
        User anotherUser = new User(userId, userName, email);
        userRepository.addUser(userId, anotherUser);
        //when
        List<User> users = userRepository.getUsers();
        //then
        assertThat(users)
                .isNotNull()
                .hasSize(2)
                .isInstanceOf(List.class);
    }

    @Test
    void getUsers_success_noUsers() throws EntityAlreadyExistException {
        //when
        List<User> users = userRepository.getUsers();
        //then
        assertThat(users)
                .isNotNull()
                .hasSize(0)
                .isInstanceOf(List.class);
    }

    @Test
    void addUser_success() throws EntityAlreadyExistException {
        //when
        User addedUser = userRepository.addUser(user.getId(), user);
        //then
        assertThat(addedUser)
                .isNotNull()
                .isInstanceOf(User.class)
                .isEqualTo(user);
    }

    @Test
    void addUser_failure_sameId() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        Long userId = 1L;
        String userName = "anothername";
        String email = "another@mail.ru";
        User anotherUser = new User(userId, userName, email);
        //then
        assertThatThrownBy(() ->
                userRepository.addUser(userId, anotherUser))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addUser_failure_duplicateEmail() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        Long userId = 2L;
        String userName = "anothername";
        String email = "usermail@mail.ru";
        User anotherUser = new User(userId, userName, email);
        //then
        assertThatThrownBy(() ->
                userRepository.addUser(userId, anotherUser))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessageContaining("User with email usermail@mail.ru already exists");
    }

    @Test
    void updateUser_success() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        String newName = "newName";
        User userUpdate = User.builder()
                .name(newName)
                .build();
        User updatedUser = userRepository.updateUser(user.getId(), userUpdate);
        //then
        assertThat(updatedUser)
                .isNotNull()
                .isInstanceOf(User.class);
        assertThat(updatedUser.getName())
                .isEqualTo(newName);
    }

    @Test
    void updateUser_failure_nonExistingUser() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        String newName = "newName";
        Long wrongId = -999L;
        User userUpdate = User.builder()
                .name(newName)
                .build();
        //then
        assertThatThrownBy(() ->
                userRepository.updateUser(wrongId, userUpdate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("user -999 do not exists");
    }

    @Test
    void deleteUser_success() throws EntityAlreadyExistException {
        //given
        userRepository.addUser(user.getId(), user);
        //when
        userRepository.deleteUser(user.getId());
        List<User> users = userRepository.getUsers();
        //then
        assertThat(users)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }
}
