package ru.practicum.shareit.user.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Repository
public class InMemoryUserStorageImp implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private Long id = 0L;


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {

        User updateUser = getUser(userId);
        final String emailUser = updateUser.getEmail();

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (!isUserExistsByEmail(user.getEmail())) {
                updateUser.setEmail(user.getEmail());
            } else
                throw new UserAlreadyExist("Такой email уже существует.");
        }

        emails.remove(emailUser);
        emails.put(user.getEmail(), user.getId());
        user.setId(userId);
        users.put(userId, updateUser);

        return updateUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        emails.remove(getUser(userId).getEmail());
        users.remove(userId);
        return !users.containsKey(userId);
    }

    @Override
    public Boolean isUserExistsById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Boolean isUserExistsByEmail(String email) {
        return emails.containsKey(email);
    }
}
