package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUser(Long userId);

    User addUser(User user);

    User updateUser(Long userId, User user);

    Boolean deleteUser(Long userId);

    Boolean isUserExistsById(Long userId);

    Boolean isUserExistsByEmail(String email);
}
