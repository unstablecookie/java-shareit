package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(
        properties = { "spring.datasource.driverClassName=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit",
                "spring.datasource.username=test",
                "spring.datasource.password=test"}
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestUserServiceInt {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final UserService userService;
    private User user;
    private UserDto userDto;
    private Long userId = 1L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";

    @BeforeEach
    private void init() {
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void getUser_success() {
        //given
        userService.addUser(userId, userDto);
        //when
        UserDto returnedUser = userService.getUser(userId);
        User queryUser = entityManager.createQuery("SELECT u FROM User u", User.class)
                .getSingleResult();
        //then
        assertThat(returnedUser)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(UserMapper.toUserDto(queryUser));
    }

    @Test
    void getUsers_success() {
        //given
        userService.addUser(userId, userDto);
        //when
        List<UserDto> users = userService.getUsers();
        List<User> getUsers = entityManager.createQuery("select u from User as u ", User.class).getResultList();
        //then
        assertThat(users)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(users.get(0))
                .isEqualTo(UserMapper.toUserDto(getUsers.get(0)));
    }

    @Test
    void addUser_success() {
        //when
        UserDto addedUser = userService.addUser(userId, userDto);
        User queryUser = entityManager.createQuery("SELECT u FROM User u", User.class)
                .getSingleResult();
        //then
        assertThat(addedUser)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(UserMapper.toUserDto(queryUser));
    }

    @Test
    void updateUser_success() {
        //given
        userService.addUser(userId, userDto);
        //when
        String newName = "new name";
        userDto.setName(newName);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        User queryUser = entityManager.createQuery("SELECT u FROM User u", User.class)
                .getSingleResult();
        //then
        assertThat(updatedUser)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
        assertThat(updatedUser.getName())
                .isEqualTo(newName);
    }

    @Test
    void deleteUser_success() {
        //given
        userService.addUser(userId, userDto);
        //when
        userService.deleteUser(userId);
        List<User> users = entityManager.createQuery("select u from User as u ", User.class).getResultList();
        //then
        assertThat(users)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }
}
