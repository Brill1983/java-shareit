package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Validated({Create.class}) UserDto userDto) {
        log.info("В метод saveUser передан userDto.name {}, userDto.email {}", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated({Update.class}) UserDto userDto,
                                             @PathVariable long userId) {
        log.info("В метод updateUser передан userId {}, userDto.name {}, userDto.email {}",
                userId, userDto.getName(), userDto.getEmail());
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("В метод getUserById передан userId {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Вызван метод getAllUsers");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("В метод deleteUser передан userId {}", userId);
        userClient.deleteUser(userId);
    }

}
