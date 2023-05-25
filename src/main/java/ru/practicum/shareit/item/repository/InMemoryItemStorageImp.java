package ru.practicum.shareit.item.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Primary
@Repository
public class InMemoryItemStorageImp implements ItemStorage{
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
}
