package ru.practicum.shareit.itemRequest.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper.mapToItemRequest;
import static ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper.mapToItemRequestResponseDto;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь c id %d не найден", userId)));

        ItemRequest itemRequest = mapToItemRequest(itemRequestDto, user);
        itemRequestRepository.save(itemRequest);

        return mapToItemRequestResponseDto(itemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь c id %d не найден", userId)));
        List<ItemRequestResponseDto> itemsRequest = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(user.getId())
                .stream()
                .map(ItemRequestMapper::mapToItemRequestResponseDto)
                .collect(Collectors.toList());
        itemsRequest.forEach(this::setItems);

        return itemsRequest;
    }

    @Override
    public List<ItemRequestResponseDto> getAll(int from, int size, Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Не правильно переданы параметры поиска, индекс первого элемента не может" +
                    " быть меньше нуля а размер страницы должен быть больше нуля");
        }
        final PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("created").descending());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь c id %d не найден", userId)));
        List<ItemRequestResponseDto> itemsRequest = itemRequestRepository.findAll(user.getId(), pageRequest)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestResponseDto)
                .collect(Collectors.toList());
        itemsRequest.forEach(this::setItems);

        return itemsRequest;
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Пользователь c id %d не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Запрос с id %d не найден", requestId)));
        ItemRequestResponseDto itemRequestResponseDto = mapToItemRequestResponseDto(itemRequest);
        setItems(itemRequestResponseDto);

        return itemRequestResponseDto;
    }

    private void setItems(ItemRequestResponseDto itemRequestResponseDto) {
        itemRequestResponseDto.setItems(itemRepository.findAllByRequestId(itemRequestResponseDto.getId())
                .stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList()));
    }
}
