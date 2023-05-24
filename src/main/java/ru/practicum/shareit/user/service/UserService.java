package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private static final String NOT_FOUND = "User not found.";
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUser(Long userId) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(NOT_FOUND);
        }
        return userRepository.getUser(userId);
    }

    public User addUser(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user body.");
        }
        if (userRepository.isUserExistsByEmail(user.getEmail())) {
            throw new UserAlreadyExist("User already exist.");
        }
        return userRepository.addUser(user);
    }

    public User updateUser(Long userId, User user) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(NOT_FOUND);
        }
        return userRepository.updateUser(userId, user);
    }

    public Boolean deleteUser(Long userId) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(NOT_FOUND);
        }
        return userRepository.deleteUser(userId);
    }
}
