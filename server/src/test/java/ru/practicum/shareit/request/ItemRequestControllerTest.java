package ru.practicum.shareit.request;

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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto savedItemRequestDto;
    private final Long requestorId = 1L;

    @BeforeEach
    void setup() {
        itemRequestDto = ItemRequestDto.builder()
                .description("desc")
                .build();
        User requestor = User.builder()
                .id(requestorId)
                .name("requestor")
                .email("email@ya.ru")
                .build();
        savedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestor(requestor)
                .description("desc")
                .build();
    }

    @SneakyThrows
    @Test
    void addRequest_whenInputValid_thenSavedItemRequestDto() {

        when(itemRequestService.saveItemRequest(itemRequestDto, requestorId)).thenReturn(savedItemRequestDto);

        mvc.perform(post("/requests")
                        .header(SHARER_USER_ID_HEADER, requestorId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(savedItemRequestDto)));

    }
}