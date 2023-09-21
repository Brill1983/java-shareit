package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    private UserRepository userRepository;

    private ValidationService validationService;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        validationService = new ValidationService(userRepository);
    }

    @Test
    void checkUser() {
        User user = new User(1L, "Иван Иванович", "ii@mail.ru");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User methodUser = validationService.checkUser(1L);

        assertThat(methodUser.getId(), equalTo(user.getId()));
        assertThat(methodUser.getName(), equalTo(user.getName()));
        assertThat(methodUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void checkUserFail() {
        User user = new User(1L, "Иван Иванович", "ii@mail.ru");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        try{
            validationService.checkUser(1L);
        } catch(ElementNotFoundException e) {
            assertThat(e.getMessage(), equalTo("Пользователь с ID " + 1L + " не зарегистрирован"));
        }
    }
}