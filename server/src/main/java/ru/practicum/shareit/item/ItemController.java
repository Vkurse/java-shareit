package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на получение всех объектов пользователя с id {}", userId);
        List<ItemDto> items = service.findAll(userId, from, size);
        log.info("Ответ отправлен{}", items);
        return items;
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Пришел /GET запрос на получение объекта с id {}", id);
        ItemDto item = service.findById(id, ownerId);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody ItemDto itemDto) {
        log.info("Пришел /POST запрос на создание объекта {} от пользователя с id {}", itemDto, id);
        ItemDto item = service.create(itemDto, id);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел /PATCH запрос на обновление объекта на {}, с id {}, и id {} пользователя", itemDto, itemId, userId);
        ItemDto item = service.update(itemDto, itemId, userId);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на поиск объекта {}", text);
        List<ItemDto> items = service.search(text, from, size);
        log.info("Ответ отправлен {}", items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Пришел /POST запрос на добавление комментария предмету {} от пользователя с id {}", itemId, userId);
        CommentDto comment = service.addComment(itemId, userId, commentDto);
        log.info("Ответ отправлен {}", comment);
        return comment;
    }
}
