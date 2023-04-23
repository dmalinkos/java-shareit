package ru.practicum.shareit.user.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.DuplicateConflictException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.*;

@Repository
@Validated
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 1L;
    private final Set<String> emails = new HashSet<>();
    private final String userNotExistMsg = "User with 'id = %d' not exist";
    private final String duplicateErrorMsg = "User with 'email = %s' already exist";

    @Override
    public User save(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException();
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User findById(Long userId) {
        isExist(userId);
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }


    @Override
    public User patch(Long userId, UserDto userDto) {
        isExist(userId);
        User patchedUser = users.get(userId);
        if (userDto.getName() != null) {
            patchedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            emails.remove(patchedUser.getEmail());
            if (emails.contains(userDto.getEmail())) {
                throw new DuplicateConflictException(String.format(duplicateErrorMsg, userDto.getEmail()));
            }
            patchedUser.setEmail(userDto.getEmail());
        }
        return savePatchedUser(patchedUser);
    }

    @Override
    public User deleteById(Long userId) {
        isExist(userId);
        User deletedUser = users.remove(userId);
        emails.remove(deletedUser.getEmail());
        return deletedUser;
    }

    private Long generateId() {
        return generatedId++;
    }

    private User savePatchedUser(@Valid User user) {
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public void isExist(Long userId) {
        if (!users.containsKey(userId)) throw new EntityNotExistException(String.format(userNotExistMsg, userId));
    }
}
