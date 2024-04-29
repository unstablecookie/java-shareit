package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

public class ItemRequestRepositoryTest {
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void init() {
        Long userId = 1L;
        String userName = "username";
        String email = "usermail@mail.ru";
        user = new User(userId, userName, email);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("super very")
                .requestor(user)
                .created(LocalDateTime.of(2020, 11, 27, 11, 11, 11))
                .build();
        itemRequestRepository = new ItemRequestRepositoryInMemory();
    }

    @Test
    void addItemRequest_success() {
        //when
        ItemRequest request = itemRequestRepository.addItemRequest(itemRequest);
        //then
        assertThat(request)
                .isNotNull()
                .isInstanceOf(ItemRequest.class)
                .isEqualTo(itemRequest);
    }

    @Test
    void getItemRequest_success() {
        //given
        itemRequestRepository.addItemRequest(itemRequest);
        //when
        ItemRequest retrievedItemRequest = itemRequestRepository.getItemRequest(itemRequest.getId());
        //then
        assertThat(retrievedItemRequest)
                .isNotNull()
                .isInstanceOf(ItemRequest.class)
                .isEqualTo(itemRequest);
    }

    @Test
    void getItemRequest_failure_withWrongId() {
        //given
        itemRequestRepository.addItemRequest(itemRequest);
        //when
        Long wrongId = -999L;
        ItemRequest wrongIdItemRequest = itemRequestRepository.getItemRequest(wrongId);
        //then
        assertThat(wrongIdItemRequest)
                .isNull();
    }

    @Test
    void deleteItemRequest_success() {
        //given
        itemRequestRepository.addItemRequest(itemRequest);
        //when
        itemRequestRepository.deleteItemRequest(itemRequest.getId());
        ItemRequest deletedItemRequest = itemRequestRepository.getItemRequest(itemRequest.getId());
        //then
        assertThat(deletedItemRequest)
                .isNull();
    }
}
