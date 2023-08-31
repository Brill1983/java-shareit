package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImp /*implements UserRepository*/ {



//    private final Map<Long, User> userRepository = new HashMap<>();
//    private Long userCounter = 0L;
//
//    @Override
//    public User createUser(User user) {
//        user.setId(++userCounter);
//        userRepository.put(user.getId(), user);
//        return userRepository.get(user.getId());
//    }
//
//    @Override
//    public User updateUser(User user) {
//        User userFromRep = userRepository.get(user.getId());
//        Optional.ofNullable(user.getName()).ifPresent(userFromRep::setName);
//        Optional.ofNullable(user.getEmail()).ifPresent(userFromRep::setEmail);
//        return userFromRep;
//    }
//
//    @Override
//    public void deleteUser(long userId) {
//        userRepository.remove(userId);
//    }
//
//    @Override
//    public Optional<User> getUserById(long id) {
//        return userRepository.containsKey(id) ? Optional.of(userRepository.get(id)) : Optional.empty();
//    }
//
//    @Override
//    public List<User> getAllUsers() {
//        return new ArrayList<>(userRepository.values());
//    }
//
//    @Override
//    public List<String> getUsersEmails() {
//        return userRepository.values().stream()
//                .map(User::getEmail)
//                .collect(Collectors.toList());
//    }
}
