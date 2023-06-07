package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getItems(Long userId);

    ItemInfoDto getItem(Long itemId, Long userId);

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    void deleteItem(Long itemId);

    List<ItemDto> searchItems(String text);
}