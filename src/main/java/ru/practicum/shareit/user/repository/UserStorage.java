package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    User updateUser(Long id, User user);

    Boolean deleteUser(Long id);

    Boolean isUserExistsById(Long id);
}
