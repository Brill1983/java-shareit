package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ElementNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userFromRepos = userRepository.save(user);
        return UserMapper.toUserDto(userFromRepos);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        User userFromDto = UserMapper.toUser(userDto, user);
        userFromDto.setId(userId);
        return UserMapper.toUserDto(userRepository.save(userFromDto));
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Пользователь с ID " + userId + " не зарегистрирован"));
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
