package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EntityAlreadyExist;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository repository;

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        return repository.findById(userId).map(UserMapper::toUserDto)
                .orElseThrow(() ->
                        new ObjectNotFoundException("User not found."));
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user body.");
        }
        if (repository.existsUserByEmail(user.getEmail())) {
            repository.save(UserMapper.toUser(user));
            throw new EntityAlreadyExist("User already exist.");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        User updateUser = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found."));
        return UserMapper.toUserDto(repository.save(userPatchNameAndEmail(updateUser, user)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (!repository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found.");
        }
        repository.deleteById(userId);
    }

    private User userPatchNameAndEmail(User updatedUser, UserDto user) {

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (!repository.existsUserByEmail(user.getEmail())) {
                updatedUser.setEmail(user.getEmail());
            } else {
                throw new EntityAlreadyExist("Email already exists.");
            }
        }
        return updatedUser;
    }
}