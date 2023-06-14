package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotExistException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userDto;
    private UserDto savedUserDto;
    private UserDto patchUserDto;
    private UserDto patchedUserDto;
    private User patchedUser;


    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();
        userDto = UserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();
        savedUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();

        patchedUser = User.builder()
                .id(1L)
                .name("nameUpdated")
                .email("emailUpdated@email.ru")
                .build();

        patchUserDto = UserDto.builder()
                .name("nameUpdated")
                .email("emailUpdated@email.ru")
                .build();

        patchedUserDto = UserDto.builder()
                .id(1L)
                .name("nameUpdated")
                .email("emailUpdated@email.ru")
                .build();
    }

    @Test
    void save_whenInputValid_thenSavedUser() {

        when(userRepository.save(any())).thenAnswer(
                invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0, User.class);
                    user.setId(1L);
                    return user;
                }
        );

        UserDto testSavedUserDto = userService.save(userDto);

        assertEquals(savedUserDto.toString(), testSavedUserDto.toString());
        verify(userRepository, times(1)).save(any());

    }

    @Test
    void patchById_whenInputValid_thenPatchedUserDto() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto testPatchedUserDto = userService.patchById(1L, patchUserDto);

        assertEquals(patchedUserDto.toString(), testPatchedUserDto.toString());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(patchedUser);
    }

    @Test
    void patchById_whenInvalidUserId_thenEntityNotExistException() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class, () -> userService.patchById(300L, patchUserDto));
        verify(userRepository, times(1)).findById(300L);
        verify(userRepository, never()).save(any());

    }

    @Test
    void patchById_whenPatchUserDtoNameIsNull_thenPatchedUserDto() {

        patchUserDto = UserDto.builder()
                .email("emailUpdated@email.ru")
                .build();
        patchedUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("emailUpdated@email.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto testPatchedUserDto = userService.patchById(1L, patchUserDto);

        assertEquals(patchedUserDto.toString(), testPatchedUserDto.toString());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(patchedUser);

    }

    @Test
    void patchById_whenPatchUserDtoNameIsBlank_thenPatchedUserDto() {

        patchUserDto = UserDto.builder()
                .name("")
                .email("emailUpdated@email.ru")
                .build();
        patchedUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("emailUpdated@email.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto testPatchedUserDto = userService.patchById(1L, patchUserDto);

        assertEquals(patchedUserDto.toString(), testPatchedUserDto.toString());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(patchedUser);

    }

    @Test
    void patchById_whenPatchUserDtoEmailIsNull_thenPatchedUserDto() {

        patchUserDto = UserDto.builder()
                .name("nameUpdated")
                .build();
        patchedUserDto = UserDto.builder()
                .id(1L)
                .name("nameUpdated")
                .email("email@email.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto testPatchedUserDto = userService.patchById(1L, patchUserDto);

        assertEquals(patchedUserDto.toString(), testPatchedUserDto.toString());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(patchedUser);

    }

    @Test
    void patchById_whenPatchUserDtoEmailIsBlank_thenPatchedUserDto() {

        patchUserDto = UserDto.builder()
                .name("nameUpdated")
                .email("")
                .build();
        patchedUserDto = UserDto.builder()
                .id(1L)
                .name("nameUpdated")
                .email("email@email.ru")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto testPatchedUserDto = userService.patchById(1L, patchUserDto);

        assertEquals(patchedUserDto.toString(), testPatchedUserDto.toString());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(patchedUser);
    }

    @Test
    void findById_whenInputValid_thenSavedUserDto() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto foundedUserDto = userService.findById(user.getId());

        assertEquals(savedUserDto.toString(), foundedUserDto.toString());
        verify(userRepository, only()).findById(user.getId());

    }

    @Test
    void findById_whenInvalidUserId_thenSavedUserDto() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotExistException.class,
                () -> userService.findById(300L));
        verify(userRepository, only()).findById(anyLong());

    }

    @Test
    void findAll_whenInputValid_thenUserDtoList() {

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.findAll();

        assertEquals(1, userDtoList.size());
        assertEquals(savedUserDto, userDtoList.get(0));
    }

    @Test
    void deleteById() {

        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteById(anyLong());

        verify(userRepository, only()).deleteById(anyLong());

    }

    @Test
    void checkIfUserExists_whenUserExist_thenNothing() {

        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.checkIfUserExists(anyLong());
        verify(userRepository, only()).existsById(anyLong());

    }

    @Test
    void checkIfUserExists_whenUserNotExist_thenEntityNotExistException() {

        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotExistException.class,
                () -> userService.checkIfUserExists(anyLong()));
        verify(userRepository, only()).existsById(anyLong());

    }
}