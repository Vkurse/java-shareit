package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;


    @GetMapping
    public List<Item> getItems(@RequestHeader(USERID_HEADER) Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public Item addItem(@RequestHeader(USERID_HEADER) Long userId, @RequestBody Item item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(USERID_HEADER) Long userId,
                           @PathVariable Long itemId,
                           @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public Boolean deleteItem(@PathVariable Long itemId) {
        return itemService.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}