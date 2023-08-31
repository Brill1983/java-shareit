package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // save
    // delete

    User updateUserBy(User user);

//    User createUser(User user);
//
//    User updateUser(User user);
//
//    void deleteUser(long userId);
//
//    Optional<User> getUserById(long id);
//
//    List<String> getUsersEmails();
//
//    List<User> getAllUsers();
}
