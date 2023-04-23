package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto patchById(Long userId, UserDto patchedUser);

    UserDto findById(Long userId);

    List<UserDto> findAll();

    UserDto deleteById(Long userId);
}
