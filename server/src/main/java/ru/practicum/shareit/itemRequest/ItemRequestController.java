package ru.practicum.shareit.itemRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пришел /POST запрос на создание запроса {} от пользователя с id {}", itemRequestDto, userId);
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.create(userId, itemRequestDto);
        log.info("Ответ отправлен {}", itemRequestResponseDto);
        return itemRequestResponseDto;
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел /GET запрос на получение списка запросов от пользователя с id {}", userId);
        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestService.getAllByUser(userId);
        log.info("Ответ отправлен {}", itemRequestResponseDtos);
        return itemRequestResponseDtos;
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(@RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел /GET запрос на получение списка запросов от пользователя с id {} созданных другим пользователем",
                userId);
        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestService.getAll(from, size, userId);
        log.info("Ответ отправлен {}", itemRequestResponseDtos);
        return itemRequestResponseDtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел /GET запрос на получение запроса по id");
        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.getById(requestId, userId);
        log.info("Ответ отправлен {}", itemRequestResponseDto);
        return itemRequestResponseDto;
    }
}
