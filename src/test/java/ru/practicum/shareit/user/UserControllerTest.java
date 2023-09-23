package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        when(userService.createUser(any()))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .createUser(any());
    }

    @Test
    void saveUserWrongEmail() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "iimail.ru");
        when(userService.createUser(any()))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any());
    }

    @Test
    void saveUserEmailIsNull() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", null);
        when(userService.createUser(any()))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any());
    }


    @Test
    void updateUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserWrongEmail() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "iimail.ru");
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any());
    }

    @Test
    void updateUserNullEmail() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", null);
        when(userService.updateUser(any(), anyLong()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, never())
                .createUser(any());
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        ;

        verify(userService, times(1))
                .getUserById(anyLong());

    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        List<UserDto> usersList = List.of(userDto);
        when(userService.getAllUsers())
                .thenReturn(usersList);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(usersList)));

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void deleteUserById() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUser(userId);
    }
}