package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(@Valid User user);

    Optional<User> findById(Long userId);

    List<User> findAll();

    Optional<User> patch(Long userid, UserDto userDto);

    User deleteById(Long userId);

    boolean checkIfUserExists(Long userId);
}
