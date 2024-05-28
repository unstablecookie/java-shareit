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
    private int defaultFrom = 0;
    private int defaultSize = 10;

    @BeforeEach
    private void init() {
        itemDto = createItemDto();
        item = createItem();
        item.setId(1L);
        owner = createUser("Peter", "iown@mail.ts");
        owner.setId(1L);
        userService.addUser(UserMapper.toUserDto(owner));
        itemService.addItem(owner.getId(), itemDto);
    }

    @Test
    void addItem_success() {
        //when
        ItemDto addedItemDto = itemService.addItem(owner.getId(), itemDto);
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
        ItemDto updatedItemDto = itemService.updateItem(owner.getId(), itemDto, item.getId());
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
        ItemWithBookingsDto returnedItemWithBookingsDto = itemService.getItem(owner.getId(), item.getId());
        Item queryItem = entityManager.createQuery("SELECT i FROM Item i where i.id = : id", Item.class)
                .setParameter("id", item.getId())
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
        List<ItemWithBookingsDto> items = itemService.getUserItems(owner.getId(), defaultFrom, defaultSize);
        List<Item> queryItems =
                entityManager.createQuery("select i from Item as i where i.owner = :id order by i.id", Item.class)
                        .setParameter("id", owner.getId())
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
        Item searchedItem = entityManager.find(Item.class, item.getId());
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
        itemService.deleteItem(item.getId());
        List<Item> items = entityManager.createQuery("SELECT i FROM Item i", Item.class)
                .getResultList();
        //then
        assertThat(items)
                .hasSize(0);
    }

    private User createUser(String userName, String userEmail) {
        return User.builder()
                .name(userName)
                .email(userEmail)
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
    }
}
