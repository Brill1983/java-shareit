package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
    }

    @Test
    void toUserDto() {
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUser() {
        User user = UserMapper.toUser(userDto);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void userDtoToUserWithUser() {
        userDto.setEmail(null);

        User user2 = new User(1L, "Петр Петрович", "pp@mail.ru");

        User mappedUser = UserMapper.toUser(userDto, user2);

        assertEquals(1L, mappedUser.getId());
        assertEquals("Иван Иванович", mappedUser.getName());
        assertEquals("pp@mail.ru", mappedUser.getEmail());
    }
}