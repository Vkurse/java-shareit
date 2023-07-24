package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(Long userId, int from, int size);

    ItemDto findById(Long id, Long ownerId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> search(String text,  int from, int size);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
