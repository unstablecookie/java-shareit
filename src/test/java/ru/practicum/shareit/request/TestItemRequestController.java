package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ItemRequestController.class)
public class TestItemRequestController {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private final String headerXSharerUserId = "X-Sharer-User-Id";
    private String userName = "Ken";
    private String itemDescription = "very thing";
    private Long itemRequestId = 1L;
    private LocalDateTime created = LocalDateTime.of(2024, 1, 1, 1, 1, 1);

    @BeforeEach
    private void init() {
        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .created(created)
                .description(itemDescription)
                .available(Boolean.FALSE)
                .build();
    }

    @Test
    void addItemRequest_success() throws Exception {
        //when
        when(itemRequestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDto);
        //given
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestId), Long.class))
                .andExpect(jsonPath("$.description", is(itemDescription)))
                .andExpect(jsonPath("$.available", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void updateItemRequest_success() throws Exception {
        //when
        when(itemRequestService.updateItemRequest(anyLong(), any(), anyLong())).thenReturn(itemRequestDto);
        //given
        mvc.perform(patch("/requests/{itemRequestId}", itemRequestId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestId), Long.class))
                .andExpect(jsonPath("$.description", is(itemDescription)))
                .andExpect(jsonPath("$.available", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void getItemRequest_success() throws Exception {
        //when
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);
        //given
        mvc.perform(get("/requests/{itemRequestId}", itemRequestId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestId), Long.class))
                .andExpect(jsonPath("$.description", is(itemDescription)))
                .andExpect(jsonPath("$.available", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.created", is(created.toString())));
    }

    @Test
    void getUserItemRequests_success() throws Exception {
        //when
        when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(List.of(itemRequestDto));
        //given
        mvc.perform(get("/requests")
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestId), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDescription)))
                .andExpect(jsonPath("$[0].available", is(Boolean.FALSE)))
                .andExpect(jsonPath("$[0].created", is(created.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllRequests_success() throws Exception {
        //when
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        //given
        mvc.perform(get("/requests/all")
                        .header(headerXSharerUserId, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestId), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDescription)))
                .andExpect(jsonPath("$[0].available", is(Boolean.FALSE)))
                .andExpect(jsonPath("$[0].created", is(created.toString())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteItemRequest_success() throws Exception {
        //given
        mvc.perform(delete("/requests/{itemRequestId}", itemRequestId)
                        .header(headerXSharerUserId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
