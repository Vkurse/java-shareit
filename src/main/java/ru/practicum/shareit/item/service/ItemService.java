package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
public class ItemService {

    private static final String ITEM_NOT_FOUND = "Item not found.";
    private static final String USER_NOT_FOUND = "User not found.";
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public List<Item> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    public Item getItem(Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.getItem(itemId);
    }

    public Item addItem(Long userId, Item item) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(USER_NOT_FOUND);
        }
        if (ItemValidator.itemCheck(item)) {
            throw new InvalidEntityException("Invalid item body.");
        }
        return itemStorage.addItem(userId, item);
    }

    public Item updateItem(Long userId, Long itemId, Item item) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.updateItem(userId, itemId, item);
    }

    public Boolean deleteItem(Long itemId) {
        if (!itemStorage.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        return itemStorage.deleteItem(itemId);
    }

    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
