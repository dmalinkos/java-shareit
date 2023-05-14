package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto patchById(Long userId, UserDto patchedUser);

    UserDto findById(Long userId);

    List<UserDto> findAll();

    void deleteById(Long userId);

    void checkIfUserExists(Long userId);
}
