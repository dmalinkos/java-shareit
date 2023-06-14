package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@ya.ru")
                .build();
    }

    @Test
    void toUserDto() {

        UserDto mappedUser = UserMapper.toUserDto(user);

        assertEquals(userDto.toString(), mappedUser.toString());

    }

    @Test
    void toUser() {

        User mappedUserDto = UserMapper.toUser(userDto);

        assertEquals(user.toString(), mappedUserDto.toString());

    }
}