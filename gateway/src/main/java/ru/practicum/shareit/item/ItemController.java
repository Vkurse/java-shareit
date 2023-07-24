package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на получение всех объектов пользователя с id {}", userId);
        ResponseEntity<Object> items = itemClient.findAll(userId, from, size);
        log.info("Ответ отправлен{}", items);
        return items;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Пришел /GET запрос на получение объекта с id {}", id);
        ResponseEntity<Object> item = itemClient.findById(id, ownerId);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long id, @Valid @RequestBody ItemRequestDto itemDto) {
        log.info("Пришел /POST запрос на создание объекта {} от пользователя с id {}", itemDto, id);
        ResponseEntity<Object> item = itemClient.create(id, itemDto);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemRequestDto itemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел /PATCH запрос на обновление объекта на {}, с id {}, и id {} пользователя", itemDto, itemId, userId);
        ResponseEntity<Object> item = itemClient.update(itemId, userId, itemDto);
        log.info("Ответ отправлен {}", item);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на поиск объекта {}", text);
        ResponseEntity<Object> items = itemClient.search(text, from, size);
        log.info("Ответ отправлен {}", items);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Пришел /POST запрос на добавление комментария предмету {} от пользователя с id {}", itemId, userId);
        ResponseEntity<Object> comment = itemClient.addComment(itemId, userId, commentDto);
        log.info("Ответ отправлен {}", comment);
        return comment;
    }
}
