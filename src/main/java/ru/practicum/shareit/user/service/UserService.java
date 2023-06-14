package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUser(Long userId);

    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);
}