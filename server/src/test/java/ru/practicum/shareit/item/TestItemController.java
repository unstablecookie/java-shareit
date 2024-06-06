package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoFull;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemController.class)
public class TestItemController {
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto;
    private Item item;
    private ItemWithBookingsDto itemWithBookingsDto;
    private final String headerXSharerUserId = "X-Sharer-User-Id";

    @BeforeEach
    private void init() {
        itemDto = createItemDto();
        item = createItem();
        item.setId(1L);
        itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
    }

    @Test
    void addItem_success() throws Exception {
        //given
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        //then
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void addItemComment_success() throws Exception {
        //given
        String userName = "Ken";
        String text = "it's good";
        Long commentId = 1L;
        CommentDtoFull commentDtoFull = createCommentDtoFull();
        when(commentService.addItemComment(anyLong(), any(CommentDto.class), anyLong()))
                .thenReturn(commentDtoFull);
        //then
        mvc.perform(post("/items/{item.getId()}/comment", 1)
                        .content(mapper.writeValueAsString(commentDtoFull))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentId), Long.class))
                .andExpect(jsonPath("$.text", is(text)))
                .andExpect(jsonPath("$.authorName", is(userName)));
    }

    @Test
    void updateItem_success() throws Exception {
        //given
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto);
        //then
        mvc.perform(patch("/items/{item.getId()}", item.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void getItem_success() throws Exception {
        //given
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemWithBookingsDto);
        //then
        mvc.perform(get("/items/{item.getId()}", item.getId())
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void getUserItems_success() throws Exception {
        //given
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemWithBookingsDto));
        //then
        mvc.perform(get("/items")
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchForAnItem_success() throws Exception {
        //given
        String searchText = "my search";
        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        //then
        mvc.perform(get("/items/search")
                        .header(headerXSharerUserId, 1)
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteItem_success() throws Exception {
        //then
        mvc.perform(delete("/items/{item.getId()}", item.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .build();
    }

    private CommentDtoFull createCommentDtoFull() {
        return CommentDtoFull.builder()
                .id(1L)
                .text("it's good")
                .authorName("Ken")
                .created(LocalDateTime.of(2024, 1, 1, 1, 1, 1))
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
