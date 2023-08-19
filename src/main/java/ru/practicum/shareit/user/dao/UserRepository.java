package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    Optional<User> getUserById(long id);

    List<String> getUsersEmails();

    List<User> getAllUsers();
}
