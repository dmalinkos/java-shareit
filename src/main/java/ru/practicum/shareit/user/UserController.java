package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto save(@RequestBody @Valid UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@RequestBody UserDto userDto,
                         @PathVariable Long userId) {
        return userService.patchById(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return new ArrayList<>(userService.findAll());
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
