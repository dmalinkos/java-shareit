package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private UserDto sevedUserDto;

    @BeforeEach
    void setup() {
        userDto = UserDto.builder()
                .name("name")
                .email("email@ya.ru")
                .build();

        sevedUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void save_whenInputValid_thenUserDto() {

        when(userService.save(userDto)).thenReturn(sevedUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(sevedUserDto)));
        verify(userService, only()).save(any());

    }

    @SneakyThrows
    @Test
    void save_whenUserDtoNameIsBlank_thenConstraintViolationException() {

        userDto = UserDto.builder()
                .name("")
                .email("email@ya.ru")
                .build();
        when(userService.save(userDto)).thenReturn(sevedUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).save(any());

    }

    @SneakyThrows
    @Test
    void save_whenUserDtoEmailNotValid_thenConstraintViolationException() {

        userDto = UserDto.builder()
                .name("name")
                .email("emailya.ru")
                .build();
        when(userService.save(userDto)).thenReturn(sevedUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).save(any());

    }

    @SneakyThrows
    @Test
    void patch_whenInputValid_thenPatchedUserDto() {
        UserDto patchUserDto = UserDto.builder()
                .name("nameUpdated")
                .email("emailUpdated@ya.ru")
                .build();
        UserDto patchedUserDto = UserDto.builder()
                .id(1L)
                .name("nameUpdated")
                .email("emailUpdated@ya.ru")
                .build();
        when(userService.patchById(1L, patchUserDto)).thenReturn(patchedUserDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(patchUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpectAll(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(patchedUserDto)));
        verify(userService, only()).patchById(1L, patchUserDto);
    }

    @SneakyThrows
    @Test
    void findById_() {

        when(userService.findById(1L)).thenReturn(sevedUserDto);

        mvc.perform(get("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpectAll(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(sevedUserDto)));
        verify(userService, only()).findById(1L);

    }

    @SneakyThrows
    @Test
    void findAll() {

        when(userService.findAll()).thenReturn(List.of(sevedUserDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(sevedUserDto))));
        verify(userService, only()).findAll();

    }

    @SneakyThrows
    @Test
    void deleteById() {

        doNothing().when(userService).deleteById(anyLong());

        mvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
        verify(userService, only()).deleteById(anyLong());

    }
}