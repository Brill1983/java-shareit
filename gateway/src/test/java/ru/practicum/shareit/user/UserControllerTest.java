package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private UserClient userClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    public void itemCreate() {
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        response = new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Test
    void saveUser() throws Exception {
        when(userClient.createUser(any()))
                .thenReturn(response);
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

        verify(userClient, times(1))
                .createUser(any());
    }

    @Test
    void saveUserWrongEmail() throws Exception {
        userDto.setEmail("ssss.su");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void saveUserEmailIsNull() throws Exception {
        userDto.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }


    @Test
    void updateUser() throws Exception {
        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
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

        verify(userClient, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    void updateUserWrongEmail() throws Exception {
        userDto.setEmail("aaaaa.ru");

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void updateUserNullEmail() throws Exception {
        when(userClient.updateUser(any(), anyLong()))
                .thenReturn(response);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, never())
                .createUser(any());
    }

    @Test
    void getUserById() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(response);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userClient, times(1))
                .getUserById(anyLong());
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> usersList = List.of(userDto);
        ResponseEntity<Object> responseWithList = new ResponseEntity<>(usersList, HttpStatus.OK);

        when(userClient.getAllUsers())
                .thenReturn(responseWithList);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(usersList)));

        verify(userClient, times(1))
                .getAllUsers();
    }

    @Test
    void deleteUserById() throws Exception {
        Long userId = 1L;
        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1))
                .deleteUser(userId);
    }
}