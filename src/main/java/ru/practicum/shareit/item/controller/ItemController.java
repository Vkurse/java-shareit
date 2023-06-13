package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @GetMapping
    public List<ItemInfoDto> getItems(@RequestHeader(USERID_HEADER) Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItem(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USERID_HEADER) Long userId, @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USERID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USERID_HEADER) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}