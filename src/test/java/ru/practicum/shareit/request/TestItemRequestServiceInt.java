package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
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
public class TestItemRequestServiceInt {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private User owner;
    private User user;
    private Item item;
    private ItemDto itemDto;
    private UserDto ownerDto;
    private UserDto userDto;
    private ItemRequestDto itemRequestDto;
    private Long userId = 1L;
    private Long ownerId = 2L;
    private Long itemId = 1L;
    private String userName = "Ken";
    private String userEmail = "eken@mail.ts";
    private String ownerName = "Peter";
    private String ownerEmail = "iown@mail.ts";
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        item = Item.builder()
                .id(1L)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .owner(ownerId)
                .build();
        user = User.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        owner = User.builder()
                .id(ownerId)
                .name(ownerName)
                .email(ownerEmail)
                .build();
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .description("i want some thing")
                .build();
        userDto = UserMapper.toUserDto(user);
        ownerDto = UserMapper.toUserDto(owner);
        userService.addUser(userId, userDto);
    }

    @Test
    void addItemRequest_success() {
        //when
        ItemRequestDto addedItemRequestDto = itemRequestService.addItemRequest(userId, itemRequestDto);
        ItemRequest queryItemRequest = entityManager.createQuery("SELECT ir FROM ItemRequest ir", ItemRequest.class)
                .getSingleResult();
        //then
        assertThat(addedItemRequestDto.getDescription())
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo(ItemRequestMapper.toItemRequestDto(queryItemRequest, List.of()).getDescription());
    }

    @Test
    void updateItemRequest_success() {
        //given
        Long itemRequestId = 1L;
        itemRequestService.addItemRequest(userId, itemRequestDto);
        String changedDescription = "changed description";
        //when
        itemRequestDto.setDescription(changedDescription);
        ItemRequestDto updatedItemRequestDto = itemRequestService.updateItemRequest(itemRequestId, itemRequestDto, userId);
        ItemRequest queryItemRequest = entityManager.createQuery("SELECT ir FROM ItemRequest ir", ItemRequest.class)
                .getSingleResult();
        //then
        assertThat(updatedItemRequestDto.getDescription())
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo(queryItemRequest.getDescription());
    }

    @Test
    void getItemRequest_success() {
        //given
        Long itemRequestId = 1L;
        itemRequestService.addItemRequest(userId, itemRequestDto);
        //when
        ItemRequestDto returnedItemRequestDto = itemRequestService.getItemRequest(userId, itemRequestId);
        ItemRequest queryItemRequest = entityManager.createQuery("SELECT ir FROM ItemRequest ir", ItemRequest.class)
                .getSingleResult();
        //then
        assertThat(returnedItemRequestDto)
                .isNotNull()
                .isInstanceOf(ItemRequestDto.class)
                .isEqualTo(ItemRequestMapper.toItemRequestDto(queryItemRequest, List.of()));
    }

    @Test
    void deleteItemRequest_success() {
        //given
        itemRequestService.addItemRequest(userId, itemRequestDto);
        Long itemRequestId = 1L;
        //when
        itemRequestService.deleteItemRequest(userId, itemRequestId);
        List<ItemRequest> getUserItemRequests =
                entityManager.createQuery("select ir from ItemRequest as ir where ir.requestor.id = :id order by ir.id",
                                ItemRequest.class)
                .setParameter("id", userId)
                .getResultList();
        //then
        assertThat(getUserItemRequests)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    void getUserItemRequests_success() {
        //given
        Long itemRequestId = 1L;
        userService.addUser(ownerId, ownerDto);
        itemRequestService.addItemRequest(userId, itemRequestDto);
        itemDto.setRequestId(itemRequestId);
        //when
        itemService.addItem(ownerId, itemDto);
        List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(userId);
        List<ItemRequest> queryUserItemRequests =
                entityManager.createQuery("select ir from ItemRequest as ir where ir.requestor.id = :id order by ir.id",
                                ItemRequest.class)
                        .setParameter("id", userId)
                        .getResultList();
        //then
        assertThat(requests)
                .isNotNull()
                .hasSize(1);
        assertThat(requests.get(0))
                .isInstanceOf(ItemRequestDto.class)
                .isEqualTo(ItemRequestMapper.toItemRequestDto(queryUserItemRequests.get(0), List.of(itemDto)));
    }

    @Test
    void getAllItemRequests_success() {
        //given
        itemRequestService.addItemRequest(userId, itemRequestDto);
        Long itemRequestId = 1L;
        //when
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(userId, defaultFrom, defaultSize);
        List<ItemRequest> queryItemRequests =
                entityManager.createQuery("select ir from ItemRequest as ir where ir.requestor.id != :id ",
                                ItemRequest.class).setParameter("id", itemRequestId).getResultList();
        //then
        assertThat(requests)
                .isNotNull()
                .hasSize(queryItemRequests.size());
    }
    //given
    //when
    //then
}
