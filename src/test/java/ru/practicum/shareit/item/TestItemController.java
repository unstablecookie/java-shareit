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
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    private CommentDto commentDto;
    private Item item;
    private CommentDtoFull commentDtoFull;
    private ItemWithBookingsDto itemWithBookingsDto;
    private Long itemId = 1L;
    private String itemName = "thing";
    private String itemDescription = "very thing";
    private final String headerXSharerUserId = "X-Sharer-User-Id";
    private Long commentId = 1L;
    private String text = "it's good";

    @BeforeEach
    private void init() {
        itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .description(itemDescription)
                .available(Boolean.TRUE)
                .build();
        commentDto = CommentDto.builder()
                .id(commentId)
                .text(text)
                .build();
        item = Item.builder()
                .id(1L)
                .name("thing")
                .description("very thing")
                .available(Boolean.TRUE)
                .owner(2L)
                .build();
        itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item);
    }

    @Test
    void addItem_success() throws Exception {
        //when
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        //then
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(itemName)))
                .andExpect(jsonPath("$.description", is(itemDescription)));
    }

    @Test
    void addItemComment_success() throws Exception {
        //given
        String userName = "Ken";
        User user = User.builder()
                .id(1L)
                .name(userName)
                .email("bens@mail.ts")
                .build();
        Comment comment = Comment.builder()
                .id(commentId)
                .item(item)
                .text(text)
                .author(user)
                .created(LocalDateTime.of(2024, 1, 1, 3, 1, 1))
                .build();
        commentDtoFull = CommentMapper.toCommentDtoFull(comment, userName);
        //when
        when(commentService.addItemComment(anyLong(), any(CommentDto.class), anyLong()))
                .thenReturn(commentDtoFull);
        //then
        mvc.perform(post("/items/{itemId}/comment", 1)
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
        //when
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);
        //then
        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(itemName)))
                .andExpect(jsonPath("$.description", is(itemDescription)));
    }

    @Test
    void getItem_success() throws Exception {
        //when
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemWithBookingsDto);
        //then
        mvc.perform(get("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(itemName)))
                .andExpect(jsonPath("$.description", is(itemDescription)));
    }

    @Test
    void getUserItems_success() throws Exception {
        //when
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemWithBookingsDto));
        //then
        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemId), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemName)))
                .andExpect(jsonPath("$[0].description", is(itemDescription)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchForAnItem_success() throws Exception {
        //given
        String searchText = "my search";
        //when
        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        //then
        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .param("text", searchText)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemId), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemName)))
                .andExpect(jsonPath("$[0].description", is(itemDescription)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteItem_success() throws Exception {
        //then
        mvc.perform(delete("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    //given
    //when
    //then
}
