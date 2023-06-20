package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
        return userClient.saveUser(userDto);
    }

//    @PatchMapping("/{userId}")
//    public UserDto patch(@RequestBody UserDto userDto,
//                         @PathVariable Long userId) {
//        return userService.patchById(userId, userDto);
//    }
//
//    @GetMapping("/{userId}")
//    public UserDto findById(@PathVariable Long userId) {
//        return userService.findById(userId);
//    }
//
//    @GetMapping
//    public List<UserDto> findAll() {
//        return new ArrayList<>(userService.findAll());
//    }
//
//    @DeleteMapping("/{userId}")
//    public void deleteById(@PathVariable Long userId) {
//        userService.deleteById(userId);
//    }

}
