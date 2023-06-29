package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Booking booking;
    private Item item;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.of(2023, Month.FEBRUARY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.FEBRUARY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1L)
                .item(item)
                .text("commentText")
                .user(user)
                .created(LocalDateTime.now())
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .description("user2")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1")
                .owner(user)
                .request(itemRequest)
                .available(true)
                .build();
    }

    @Test
    void getItems() {
        Booking booking1 = booking;
        booking1.setId(2L);
        when(userRepository.existsById(user.getId())).thenReturn(true);

        when(commentRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.findAllByOwnerId(user.getId())).thenReturn(List.of(item));
        when(commentRepository.findByItem_Id(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId())).thenReturn(List.of(booking, booking1));

        List<ItemInfoDto> items = itemService.getItems(user.getId());

        assertNotNull(items);
        assertEquals(items.get(0).getId(), item.getId());
    }

    @Test
    void getItem() {
        item.setOwner(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(1L, 2L));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(2L, 1L));

        when(commentRepository.existsById(item.getId())).thenReturn(true);
        when(commentRepository.getReferenceById(item.getId())).thenReturn(comment);
        when(bookingRepository.findAllByItemIdOrderByStartAsc(anyLong()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(),
                any(), any())).thenReturn(List.of(booking));

        ItemInfoDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(result.getLastBooking().getId(), booking.getId());
        assertNull(result.getNextBooking());
    }

    @Test
    void addItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(999L, itemDto));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.toItem(itemDto));

        itemDto.setRequestId(2L);
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        itemDto.setRequestId(1L);
        ItemDto addedItem = itemService.addItem(user.getId(), itemDto);
        assertNotNull(addedItem);

        itemDto.setRequestId(null);
        addedItem = itemService.addItem(user.getId(), itemDto);
        assertNull(addedItem.getRequestId());

        assertThrows(InvalidEntityException.class, () -> {
            itemDto.setName("");
            itemService.addItem(user.getId(), itemDto);
        });
    }

    @Test
    void deleteItem() {
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(999L));

        itemService.deleteItem(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void searchItems() {
        ItemDto itemDto = ItemMapper.toDto(item);

        when(itemService.searchItems("")).thenReturn(Collections.emptyList());
        assertTrue(itemRepository.search("").isEmpty());

        when(itemService.searchItems("item1")).thenReturn(List.of(itemDto));
        assertFalse(itemRepository.search("item1").isEmpty());
    }

    @Test
    void addComment() {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(InvalidEntityException.class, () -> itemService.addComment(2L, 1L, commentDto));

        ItemInfoDto itemInfoDto = mock(ItemInfoDto.class);
        BookingItemDto bookingItemDto = BookingItemDto.builder().build();
        bookingItemDto.setId(1L);

        when(itemInfoDto.getLastBooking()).thenReturn(bookingItemDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(InvalidEntityException.class, () ->
                itemService.addComment(1L, 2L, commentDto));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);
        when(bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .id(2L)
                .owner(User.builder().id(10L).build())
                .build()));

        assertNotNull(itemService.addComment(1L, 1L, commentDto));

        assertThrows(InvalidEntityException.class, () -> {
            commentDto.setText("");
            itemService.addComment(1L, 1L, commentDto);
        });

        when(bookingRepository.findByBooker_IdAndItem_IdOrderByStartAsc(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        assertThrows(InvalidEntityException.class, () -> {
            commentDto.setText("text");
            booking.setEnd(LocalDateTime.of(2050, Month.FEBRUARY, 5, 12, 0));
            itemService.addComment(1L, 1L, commentDto);
        });

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(InvalidEntityException.class, () -> {
            item.setOwner(User.builder().id(10L).build());
            itemService.addComment(1L, 1L, commentDto);
        });
    }

    @Test
    void updateItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        ItemDto updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("itemUpdated")
                .description("itemUpdated")
                .owner(user.getId() + 1)
                .requestId(itemRequest.getId() + 1)
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertEquals(itemRequest.getId(), itemService.updateItem(1L, 1L, updatedItemDto).getRequestId());

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemDto.setOwner(1L);
            itemService.updateItem(1L, 1L, itemDto);
        });

        when(itemRepository.findById(1L)).thenReturn(Optional.of(Item.builder()
                .id(1L)
                .name("item1")
                .description("item1")
                .owner(User.builder().id(1000L).build())
                .request(itemRequest)
                .available(true)
                .build()));

        assertThrows(ObjectNotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, updatedItemDto);
        });
    }
}