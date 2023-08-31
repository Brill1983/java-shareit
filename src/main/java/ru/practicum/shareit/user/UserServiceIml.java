package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceIml implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
//        if (userRepository.getUsersEmails().contains(userDto.getEmail())) {
//            throw new EmailExistException("Пользователь с такой почтой уже зарегистрирован");
//        }
        User user = UserMapper.toUser(userDto);
        User userFromRepos = userRepository.save(user);
        return UserMapper.toUserDto(userFromRepos);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User userCheck = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));

//        if (userRepository.getUsersEmails().contains(userDto.getEmail()) && !userCheck.getEmail().equals(userDto.getEmail())) { //TODO переписать
//            throw new EmailExistException("Пользователь с такой почтой уже зарегистрирован");
//        }

        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        User userFromRep = userRepository.updateUserBy(user);
        return UserMapper.toUserDto(userFromRep);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> usersList = userRepository.findAll();
        return usersList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
