package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItems(Long userId);

    Item getItem(Long itemId);

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Boolean deleteItem(Long itemId);

    Boolean isItemExists(Long id);

    List<Item> searchItems(String text);
}