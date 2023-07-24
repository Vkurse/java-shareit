package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getAllByUser(Long userId);

    List<ItemRequestResponseDto> getAll(int from, int size, Long userId);

    ItemRequestResponseDto getById(Long requestId, Long userId);
}
