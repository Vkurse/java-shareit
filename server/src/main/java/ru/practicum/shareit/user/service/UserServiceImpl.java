package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(toUserDto(user));
        }
        return users;
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException(String.format("Пользователь с id %d не найден", id)));
        return toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto create(User user) {
        User createdUser = userRepository.save(user);
        return toUserDto(createdUser);
    }

    @Transactional
    @Override
    public UserDto update(User user, Long id) {
        User updatedUser = userRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException(String.format("Пользователь с id %d не найден", id)));
        Optional.ofNullable(user.getEmail()).ifPresent(updatedUser::setEmail);
        Optional.ofNullable(user.getName()).ifPresent(updatedUser::setName);

        return toUserDto(userRepository.save(updatedUser));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
