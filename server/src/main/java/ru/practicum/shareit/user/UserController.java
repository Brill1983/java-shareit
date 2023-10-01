package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        log.info("В метод saveUser передан userDto.name {}, userDto.email {}", userDto.getName(), userDto.getEmail());
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("В метод updateUser передан userId {}, userDto.name {}, userDto.email {}",
                userId, userDto.getName(), userDto.getEmail());
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("В метод getUserById передан userId {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Вызван метод getAllUsers");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("В метод deleteUser передан userId {}", userId);
        userService.deleteUser(userId);
    }

}
