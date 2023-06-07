package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemValidator {
    public static boolean itemCheck(ItemDto item) {
        return item.getName() == null ||
                item.getName().isBlank() ||
                item.getName().isEmpty() ||
                item.getAvailable() == null ||
                item.getDescription() == null ||
                item.getDescription().isEmpty() ||
                item.getDescription().isBlank();
    }

    public static Item itemPatch(Item fstItem, ItemDto sndItem) {

        if (sndItem.getName() != null) {
            fstItem.setName(sndItem.getName());
        }
        if (sndItem.getDescription() != null) {
            fstItem.setDescription(sndItem.getDescription());
        }
        if (sndItem.getAvailable() != null) {
            fstItem.setAvailable(sndItem.getAvailable());
        }
        return fstItem;
    }
}