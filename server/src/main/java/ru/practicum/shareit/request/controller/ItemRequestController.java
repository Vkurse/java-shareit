package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(USERID_HEADER) Long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USERID_HEADER) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest(
            @RequestHeader(USERID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return itemRequestService.getAllRequest(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USERID_HEADER) Long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }
}