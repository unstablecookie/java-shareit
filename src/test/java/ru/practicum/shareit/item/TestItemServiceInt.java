package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
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
public class TestItemServiceInt {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private UserDto ownerDto;
    private Long ownerId = 1L;
    private Long itemId = 1L;
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
        ownerDto = UserMapper.toUserDto(owner);
        userService.addUser(ownerDto);
        itemService.addItem(ownerId, itemDto);
    }

    @Test
    void addItem_success() {
        //when
        ItemDto addedItemDto = itemService.addItem(ownerId, itemDto);
        Item queryItem = entityManager.createQuery("SELECT i FROM Item i", Item.class)
                .getSingleResult();
        //then
        assertThat(addedItemDto)
                .isNotNull()
                .isInstanceOf(ItemDto.class)
                .isEqualTo(ItemMapper.toItemDto(queryItem));
    }

    @Test
    void updateItem_success() {
        //given
        itemDto.setAvailable(Boolean.FALSE);
        //when
        ItemDto updatedItemDto = itemService.updateItem(ownerId, itemDto, itemId);
        Item queryItem = entityManager.createQuery("SELECT i FROM Item i", Item.class)
                .getSingleResult();
        //then
        assertThat(updatedItemDto)
                .isNotNull()
                .isInstanceOf(ItemDto.class)
                .isEqualTo(ItemMapper.toItemDto(queryItem));
        assertThat(updatedItemDto.getAvailable())
                .isEqualTo(Boolean.FALSE);
    }

    @Test
    void getItem_success() {
        //when
        ItemWithBookingsDto returnedItemWithBookingsDto = itemService.getItem(ownerId, itemId);
        Item queryItem = entityManager.createQuery("SELECT i FROM Item i where i.id = : id", Item.class)
                .setParameter("id", itemId)
                .getSingleResult();
        ItemWithBookingsDto queryItemWithBookingsDto = ItemMapper.toItemWithBookingsDto(queryItem);
        queryItemWithBookingsDto.setComments(List.of());
        //then
        assertThat(returnedItemWithBookingsDto)
                .isNotNull()
                .isInstanceOf(ItemWithBookingsDto.class)
                .isEqualTo(queryItemWithBookingsDto);
    }

    @Test
    void getUserItems_success() {
        //when
        List<ItemWithBookingsDto> items = itemService.getUserItems(ownerId, defaultFrom, defaultSize);
        List<Item> queryItems =
                entityManager.createQuery("select i from Item as i where i.owner = :id order by i.id", Item.class)
                        .setParameter("id", ownerId)
                        .getResultList();
        List<ItemWithBookingsDto> queryItemWithBookingsDtos = queryItems.stream()
                .map(x -> ItemMapper.toItemWithBookingsDto(x))
                .collect(Collectors.toList());
        queryItemWithBookingsDtos.stream().forEach(x -> x.setComments(List.of()));
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(items.get(0))
                .isEqualTo(queryItemWithBookingsDtos.get(0));
    }

    @Test
    void searchItem_success() {
        //given
        String searchText = "thi";
        //when
        List<ItemDto> items = itemService.searchItem(searchText, defaultFrom, defaultSize);
        Item searchedItem = entityManager.find(Item.class, itemId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(items.get(0))
                .isEqualTo(ItemMapper.toItemDto(searchedItem));
    }

    @Test
    void deleteItem_success() {
        //when
        itemService.deleteItem(itemId);
        List<Item> items = entityManager.createQuery("SELECT i FROM Item i", Item.class)
                .getResultList();
        //then
        assertThat(items)
                .hasSize(0);
    }
}
