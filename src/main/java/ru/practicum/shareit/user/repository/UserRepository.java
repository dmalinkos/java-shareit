package ru.practicum.shareit.user.repository;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserRepository {
    User save(@Valid User user);

    User findById(Long userId);

    List<User> findAll();

    User patch(Long userid, UserDto userDto);

    User deleteById(Long userId);
}
