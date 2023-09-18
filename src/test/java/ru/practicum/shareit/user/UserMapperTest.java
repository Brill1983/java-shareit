package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = makeUser(1L, "Иван Иванович", "ii@mail.ru");
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUser() {
        UserDto userDto = makeUserDto(1L, "Иван Иванович", "ii@mail.ru");
        User user = UserMapper.toUser(userDto);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void userDtoToUserWithUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Иван Иванович");

        User user = makeUser(1L, "Петр Петрович", "ii@mail.ru");

        User mappedUser = UserMapper.toUser(userDto, user);

        assertEquals(1L, mappedUser.getId());
        assertEquals("Иван Иванович", mappedUser.getName());
        assertEquals("ii@mail.ru", mappedUser.getEmail());
    }

    private UserDto makeUserDto(Long id, String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}