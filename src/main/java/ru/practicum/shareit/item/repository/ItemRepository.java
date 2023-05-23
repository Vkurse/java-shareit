package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemRepository implements ItemStorage{

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public ItemRepository(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<Item> getItems(Long userId) {
        return null;
    }

    @Override
    public Item getItem(Long itemId) {
        return null;
    }

    @Override
    public Item addItem(Long userId, Item item) {
        return null;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        return null;
    }

    @Override
    public Boolean deleteItem(Long itemId) {
        return null;
    }

    @Override
    public Boolean isItemExists(Long id) {
        return null;
    }

    @Override
    public List<Item> searchItems(String text) {
        return null;
    }

    private Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .available(rs.getBoolean("available"))
                .owner(userStorage.getUser(rs.getLong("owner")))
                .build();
    }
}
