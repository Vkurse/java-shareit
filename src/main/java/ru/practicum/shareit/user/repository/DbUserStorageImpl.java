package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DbUserStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getUsers() {
        final String sqlQuery = "SELECT *" +
                " FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRow);
    }

    @Override
    public User getUser(Long id) {
        final String sqlQuery = "SELECT * " +
                "FROM users " +
                "WHERE id = ? ";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRow, id);
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users (name, email) " +
                "VALUES (?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {

        User updateUser = getUser(userId);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (!isUserExistsByEmail(user.getEmail())) {
                updateUser.setEmail(user.getEmail());
            } else
                throw new UserAlreadyExist("Такой email уже существует.");
        }

        final String sqlQuery = "UPDATE users " +
                "SET name = ?, email = ? " +
                "WHERE id = ? ";

        jdbcTemplate.update(sqlQuery,
                updateUser.getName(),
                updateUser.getEmail(),
                updateUser.getId());

        return updateUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        final String sqlQuery = "DELETE FROM users " +
                "WHERE id = ? ";

        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }

    @Override
    public Boolean isUserExistsById(Long userId) {
        final String sqlQuery = "SELECT EXISTS " +
                "(SELECT * FROM users WHERE id = ?) ";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, userId));
    }

    @Override
    public Boolean isUserExistsByEmail(String email) {
        final String sqlQuery = "SELECT EXISTS(SELECT * FROM users WHERE email = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, email));
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }
}
