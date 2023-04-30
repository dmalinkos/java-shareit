package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.util.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String USER_NOT_EXIST_MSG = "User with 'id = %d' is not exist";

    @Override
    public UserDto save(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto patchById(Long userId, UserDto patchUser) {
        return userRepository.patch(userId, patchUser)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId)));
    }

    @Override
    public UserDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId)));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto deleteById(Long userId) {
        return UserMapper.toUserDto(userRepository.deleteById(userId));
    }

    @Override
    public void checkIfUserExists(Long userId) {
        if (!userRepository.checkIfUserExists(userId))
            throw new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId));
    }
}
