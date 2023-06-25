package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private static final String USER_NOT_FOUND = "User not found.";
    private final ItemRequestRepository itemRequestRepository;
    private final UserJpaRepository userJpaRepository;

    private final ItemJpaRepository itemRepository;


    public List<ItemRequestDto> getRequests(Long userId) {
        userJpaRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_NOT_FOUND));
        return itemRequestRepository.findAllByRequestorId(userId)
                .stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        itemRequestDto.setRequestor(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        User user = userJpaRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_NOT_FOUND));
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto, user);
        return this.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userJpaRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_NOT_FOUND));
        return this.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException("Request not found.")));
    }

    public List<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new InvalidEntityException("Arguments can't be negative.");
        }
        userJpaRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(USER_NOT_FOUND));

        return itemRequestRepository
                .findAllByRequestorIdIsNot(userId, PageRequest.of((from / size), size, Sort.by("created").descending()))
                .stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    private ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(itemRequest.getRequestor().getId())
                .items(putItemDtoToRequest(itemRequest))
                .build();
    }

    private List<ItemDto> putItemDtoToRequest(ItemRequest itemRequest) {
        return itemRepository.findAllByRequest_Id(itemRequest.getId()).stream()
                .map(RequestMapper::toRequestItemDto)
                .collect(Collectors.toList());
    }
}