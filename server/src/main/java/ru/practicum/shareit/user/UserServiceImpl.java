package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotExistException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final String USER_NOT_EXIST_MSG = "User with 'id = %d' is not exist";

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto patchById(Long userId, UserDto patchUserDto) {
        User patchUser = UserMapper.toUser(patchUserDto);
        User curUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId)));
        if (patchUser.getName() != null && !patchUser.getName().isBlank()) {
            curUser.setName(patchUser.getName());
        }
        if (patchUser.getEmail() != null && !patchUser.getEmail().isBlank()) {
            curUser.setEmail(patchUser.getEmail());
        }
        User patchedUser = userRepository.save(curUser);
        return UserMapper.toUserDto(patchedUser);
    }

    @Override
    public UserDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId)));
    }

    @Override
    public List<UserDto> findAll() {
        return ((Collection<User>) userRepository.findAll()).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotExistException(String.format(USER_NOT_EXIST_MSG, userId));
    }
}
