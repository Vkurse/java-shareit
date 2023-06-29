package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    ItemRequestService itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserJpaRepository userRepository;

    @Mock
    ItemJpaRepository itemRepository;

    User user;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();

    }

    @Test
    void getRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(itemRequest));

        assertEquals(itemRequestService.getRequests(user.getId()), List.of(this.toItemRequestDto(itemRequest)));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemRequestService.getRequests(user.getId());
        });
    }

    @Test
    void addRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
        ItemRequestDto result = itemRequestService.addRequest(user.getId(), itemRequestDto);
        result.setCreated(itemRequestDto.getCreated());

        assertEquals(itemRequestDto, result);
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        assertEquals(itemRequestService.getRequestById(user.getId(), 1L), toItemRequestDto(itemRequest));

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemRequestService.getRequestById(1L, 1L);
        });

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemRequestService.getRequestById(1L, 1L);
        });
    }

    @Test
    void getAllRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong(), any())).thenReturn(List.of(itemRequest));

        assertEquals(itemRequestService.getAllRequest(user.getId(), 0, 10), List.of(toItemRequestDto(itemRequest)));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemRequestService.getAllRequest(user.getId(), 0, 10);
        });

        assertThrows(InvalidEntityException.class, () -> {
            itemRequestService.getAllRequest(user.getId(), 0, -10);
        });
    }

    private List<ItemDto> putItemDtoToRequest(ItemRequest itemRequest) {
        return itemRepository.findAllByRequest_Id(itemRequest.getId()).stream()
                .map(RequestMapper::toRequestItemDto)
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
}