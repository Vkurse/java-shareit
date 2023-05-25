package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Primary
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorageImp implements ItemStorage {

    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private Long idItems = 0L;


    @Override
    public List<Item> getItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getOwner().getId().equals(userId)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(++idItems);
        item.setOwner(userService.getUser(userId));
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item updatedItem = getItem(itemId);

        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("User don't have access to this item.");
        }

        ItemValidator.itemPatch(updatedItem, item);

        return updatedItem;
    }

    @Override
    public Boolean deleteItem(Long itemId) {
        items.remove(itemId);
        return true;
    }

    @Override
    public Boolean isItemExists(Long id) {
        return items.containsKey(id);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            List<Item> result = new ArrayList<>();

            for (Item i : items.values()) {
                if (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()) {
                    result.add(i);
                }
            }
            return result;
        }
    }
}
