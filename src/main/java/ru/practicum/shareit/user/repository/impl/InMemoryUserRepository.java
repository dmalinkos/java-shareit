package ru.practicum.shareit.user.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateConflictException;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private Long generatedId = 1L;
    private final Set<String> emails = new HashSet<>();
    private static final String duplicateErrorMsg = "User with 'email = %s' already exist";

    @Override
    public User save(User user) {
        if (checkIfEmailExists(user.getEmail())) {
            throw new DuplicateConflictException(String.format(duplicateErrorMsg, user.getEmail()));
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        try {
            checkIfUserExists(userId);
            return Optional.of(users.get(userId));
        } catch (EntityNotExistException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }


    @Override
    public Optional<User> patch(Long userId, UserDto userDto) {
        if (!checkIfUserExists(userId)) {
            return Optional.empty();
        }
        User patchedUser = users.get(userId);
        if (userDto.getName() != null) {
            patchedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            emails.remove(patchedUser.getEmail());
            if (checkIfEmailExists(userDto.getEmail())) {
                throw new DuplicateConflictException(String.format(duplicateErrorMsg, userDto.getEmail()));
            }
            patchedUser.setEmail(userDto.getEmail());
        }
        return Optional.of(savePatchedUser(patchedUser));
    }

    @Override
    public User deleteById(Long userId) {
        checkIfUserExists(userId);
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

    @Override
    public boolean checkIfUserExists(Long userId) {
        return users.containsKey(userId);
    }

    public boolean checkIfEmailExists(String email) {
        return emails.contains(email);
    }
}
