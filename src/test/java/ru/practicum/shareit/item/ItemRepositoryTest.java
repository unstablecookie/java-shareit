package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class ItemRepositoryTest {
    private ItemRepository itemRepository;
    private Item item;

    @BeforeEach
    public void init() {
        Long id = 1L;
        String name = "thing";
        String description = "very thing";
        Boolean available = Boolean.TRUE;
        Long owner = 1L;
        item = Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .build();
        itemRepository = new ItemRepositoryInMemory();
    }

    @Test
    void addItem_success() {
        //when
        Item addedItem = itemRepository.addItem(item);
        //then
        assertThat(addedItem)
                .isNotNull()
                .isInstanceOf(Item.class)
                .isEqualTo(item);
    }

    @Test
    void updateItem_failure_wrongId() {
        //given
        itemRepository.addItem(item);
        //when
        Long wrongId = -999L;
        String description = "another thing";
        Item anotherItem = Item.builder()
                .id(wrongId)
                .description(description)
                .build();
        Item itemWasNotUpdated = itemRepository.getItem(item.getId());
        //then
        assertThat(itemWasNotUpdated)
                .isNotNull()
                .isInstanceOf(Item.class)
                .isEqualTo(item);
    }

    @Test
    void getItem_success() {
        //given
        Long id = 1L;
        itemRepository.addItem(item);
        //when
        Item retrievedItem = itemRepository.getItem(id);
        //then
        assertThat(retrievedItem)
                .isNotNull()
                .isInstanceOf(Item.class)
                .isEqualTo(item);
    }

    @Test
    void getItem_failure_wrongId() {
        //given
        itemRepository.addItem(item);
        //when
        Long wrongId = -999L;
        Item wrongItem = itemRepository.getItem(wrongId);
        //then
        assertThat(wrongItem)
                .isNull();
    }

    @Test
    void getUserItems_success() {
        //given
        itemRepository.addItem(item);
        //when
        Long userId = item.getOwner();
        Set<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(Set.class)
                .hasSize(1);
    }

    @Test
    void getUserItems_success_noItems() {
        //given
        Long userId = 1L;
        //when
        Set<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(Set.class)
                .hasSize(0);
    }

    @Test
    void deleteUserItems_success() {
        //given
        itemRepository.addItem(item);
        //when
        Long userId = item.getOwner();
        itemRepository.deleteUserItems(userId);
        Set<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(Set.class)
                .hasSize(0);
    }
}
