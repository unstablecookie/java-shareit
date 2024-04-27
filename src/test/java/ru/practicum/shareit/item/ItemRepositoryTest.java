package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
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
    void updateItem_success() {
        //given
        itemRepository.addItem(item);
        //when
        String description = "another thing";
        Item anotherItem = Item.builder()
                .id(1L)
                .description(description)
                .build();
        Item updatedItem = itemRepository.updateItem(anotherItem);
        //then
        assertThat(updatedItem)
                .isNotNull()
                .isInstanceOf(Item.class);
        assertThat(updatedItem.getId())
                .isEqualTo(1L);
        assertThat(updatedItem.getDescription())
                .isEqualTo(description);
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
        //then
        assertThatThrownBy(() ->
                itemRepository.updateItem(anotherItem))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("item id -999 not found");
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
        //then
        assertThatThrownBy(() ->
                itemRepository.getItem(wrongId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("item id -999 not found");
    }

    @Test
    void getUserItems_success() {
        //given
        itemRepository.addItem(item);
        //when
        Long userId = item.getOwner();
        List<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void getUserItems_success_noItems() {
        //given
        Long userId = 1L;
        //when
        List<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void searchItem_success() {
        //given
        itemRepository.addItem(item);
        //when
        String searchWord = "thing";
        List<Item> result = itemRepository.searchItem(searchWord);
        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void searchItem_success_partialWord() {
        //given
        itemRepository.addItem(item);
        //when
        String searchWord = "thi";
        List<Item> result = itemRepository.searchItem(searchWord);
        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    void searchItem_success_noResults() {
        //given
        itemRepository.addItem(item);
        //when
        String searchWord = "cloud";
        List<Item> result = itemRepository.searchItem(searchWord);
        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void deleteItem_success() {
        //given
        itemRepository.addItem(item);
        //when
        Long itemId = 1L;
        itemRepository.deleteItem(itemId);
        //then
        assertThatThrownBy(() ->
                itemRepository.getItem(itemId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("item id 1 not found");
    }

    @Test
    void deleteItem_failure_nonExistingId() {
        //given
        itemRepository.addItem(item);
        //when
        Long itemId = -999L;
        //then
        assertThatThrownBy(() ->
                itemRepository.deleteItem(itemId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("item id -999 not found");
    }

    @Test
    void deleteUserItems_success() {
        //given
        itemRepository.addItem(item);
        //when
        Long userId = item.getOwner();
        itemRepository.deleteUserItems(userId);
        List<Item> items = itemRepository.getUserItems(userId);
        //then
        assertThat(items)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    void deleteUserItems_success_noItems() {
        //when
        Long userId = 1L;
        Set<Long> itemsIds = itemRepository.deleteUserItems(userId);
        //then
        assertThat(itemsIds)
                .isNotNull()
                .isInstanceOf(Set.class)
                .hasSize(0);
    }
}
