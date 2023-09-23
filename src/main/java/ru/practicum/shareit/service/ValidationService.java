package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final UserRepository userRepository;

    public User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
    }
}
