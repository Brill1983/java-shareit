package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final Map<Long, User> userRepository = new HashMap<>();
    long userCounter = 0L;

    @Override
    public User createUser(User user) {
        user.setId(++userCounter);
        userRepository.put(user.getId(), user);
        return userRepository.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        User userFromRep = userRepository.get(user.getId());
        if (user.getName() != null) {
            userFromRep.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromRep.setEmail(user.getEmail());
        }
        return userFromRep;
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.remove(userId);
    }

    @Override
    public Optional<User> getUserById(long id) {
        if (userRepository.containsKey(id)) {
            return Optional.of(userRepository.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public List<String> getUsersEmails() {
        return userRepository.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
}
