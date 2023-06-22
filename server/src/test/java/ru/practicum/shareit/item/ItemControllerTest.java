package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemDto inputItemDto;
    private ItemDto itemDto;

    private User owner;
    private final Long userId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setup() {
        inputItemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();
        owner = User.builder()
                .id(userId)
                .name("owner")
                .email("owner@ya.ru")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .owner(owner)
                .build();
    }

    @SneakyThrows
    @Test
    void post_whenInputValid_thenItemDto() {

        when(itemService.save(eq(inputItemDto), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(SHARER_USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(inputItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        verify(itemService, only()).save(inputItemDto, userId);

    }

    @SneakyThrows
    @Test
    void postItemComment_whenInputValid_thenCommentDto() {

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorName("booker")
                .text("text")
                .build();
        CommentDto inputCommentDto = CommentDto.builder()
                .text("text")
                .build();
        when(itemService.addComment(2L, itemId, inputCommentDto)).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(SHARER_USER_ID_HEADER, 2L)
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
        verify(itemService, only()).addComment(anyLong(), anyLong(), any());

    }

    @SneakyThrows
    @Test
    void patchItem() {
        ItemDto patchItemDto = ItemDto.builder()
                .name("nameUpdated")
                .description("descUpdated")
                .available(true)
                .build();
        ItemDto patchedItemDto = ItemDto.builder()
                .id(1L)
                .name("nameUpdated")
                .description("descUpdated")
                .available(true)
                .owner(owner)
                .build();
        when(itemService.patch(patchItemDto, userId, itemId)).thenReturn(patchedItemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID_HEADER, userId)
                        .content(mapper.writeValueAsString(patchItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(patchedItemDto)));
        verify(itemService, only()).patch(any(), anyLong(), anyLong());

    }

    @SneakyThrows
    @Test
    void findAllByOwner_whenInputValid_themListOfItemDto() {

        when(itemService.findAllByOwner(userId, 0L, 10L)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header(SHARER_USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
        verify(itemService, only()).findAllByOwner(anyLong(), anyLong(), anyLong());

    }

    @SneakyThrows
    @Test
    void findById_whenInputValid_thenItemDto() {

        when(itemService.findById(itemId, userId)).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        verify(itemService, only()).findById(anyLong(), anyLong());

    }

    @SneakyThrows
    @Test
    void search_whenInputValid_thenListOfItemDto() {

        when(itemService.search("desc", 0L, 10L)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header(SHARER_USER_ID_HEADER, userId)
                        .param("text", "desc")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
        verify(itemService, only()).search(anyString(), anyLong(), anyLong());

    }

    @SneakyThrows
    @Test
    void search_whenInvalidTextParam_thenListOfItemDto() {

        mvc.perform(get("/items/search")
                        .header(SHARER_USER_ID_HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());
        verify(itemService, never()).search(anyString(), anyLong(), anyLong());

    }

}
