package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ItemServiceImplTest {

    ItemService itemService;
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void beforeEach() {
        userService = Mockito.mock(UserService.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userService,
                bookingRepository,
                commentRepository,
                itemRequestRepository
        );
    }

    @Test
    void findAllByTextTestWithEmptyList() {
        User user = new User(1L, "testName", "test@mail.com");
        String text = "";

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        List<ItemDto> results = itemService.search(text, 0, 10);

        Assertions.assertEquals(0, results.size());
    }

    @Test
    void saveTest() {
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.create(itemDto, user.getId());

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongUserId() {
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenThrow(DataNotFoundException.class);

        Exception exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertNull(exception.getMessage());
    }

    @Test
    void saveTestWithRequest() {
        User user1 = new User(1L, "testName1", "test1@mail.com");
        User user2 = new User(2L, "testName2", "test2@mail.com");
        ItemRequest itemRequest =
                new ItemRequest(1L, "testDescription", user2, LocalDateTime.now());
        Item item =
                new Item(1L, "testName", "testDescription", true, user1, itemRequest);
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user1));


        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        ItemDto foundItem = itemService.create(itemDto, user1.getId());

        Assertions.assertNotNull(foundItem);
        Assertions.assertEquals(itemDto.getName(), foundItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), foundItem.getDescription());
        Assertions.assertEquals(itemDto.getAvailable(), foundItem.getAvailable());
        Assertions.assertEquals(itemDto.getRequestId(), foundItem.getRequestId());
    }

    @Test
    void saveTestWithWrongRequestId() {
        User user = new User(1L, "testName", "test@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("testName")
                .description("testDescription")
                .available(true)
                .requestId(999L)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(DataNotFoundException.class);

        Exception exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertNull(exception.getMessage());
    }

    @Test
    void saveComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .authorName(null)
                .created(null)
                .build();
        CommentDto commentInfoDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("testName")
                .created(LocalDateTime.now())
                .build();
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        Booking booking = new Booking(null, null, null, null, null, Status.APPROVED);
        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        Mockito.when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(Status.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        CommentDto foundComment = itemService.addComment(item.getId(), 1L, commentDto);

        Assertions.assertNotNull(foundComment);
        Assertions.assertEquals(commentInfoDto.getText(), foundComment.getText());
        Assertions.assertEquals(commentInfoDto.getAuthorName(), foundComment.getAuthorName());
    }

//    @Test
//    void findAll() {
//        User user = new User(1L, "testName", "test@mail.com");
//        Item item = new Item(1L, "testName", "testDescription", true, user, null);
//        Item item2 = new Item(2L, "testName2", "testDescription2", true, user, null);
//
//        Mockito.when(userService.getById(Mockito.anyLong()))
//                .thenReturn(UserMapper.toUserDto(user));
//
//        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
//                .thenReturn(item);
//
//        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong(), Mockito.any(Pageable.class)))
//                .thenReturn(new PageImpl<>(List.of(item, item2)));
//
//        Mockito.when(itemService.findAll(Mockito.anyLong(), Mockito.anyInt(), 10))
//                .thenReturn(List.of(ItemMapper.toItemDto(item), ItemMapper.toItemDto(item2)));
//
//        ItemDto itemDto = itemService.create(ItemMapper.toItemDto(item), user.getId());
//        itemDto.setComments(Collections.emptyList());
//        ItemDto itemDto2 = itemService.create(ItemMapper.toItemDto(item2), user.getId());
//        itemDto2.setComments(Collections.emptyList());
//
//        Assertions.assertEquals(List.of(itemDto, itemDto2), itemService.findAll(user.getId(), 0, 10));
//    }

    @Test
    void findAllWithBadRequestException() {
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        Item item2 = new Item(2L, "testName2", "testDescription2", true, user, null);

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item, item2)));

        Mockito.when(itemService.findAll(Mockito.anyLong(), Mockito.anyInt(), 10))
                .thenThrow(BadRequestException.class);

        ItemDto itemDto = itemService.create(ItemMapper.toItemDto(item), user.getId());
        itemDto.setComments(Collections.emptyList());
        ItemDto itemDto2 = itemService.create(ItemMapper.toItemDto(item2), user.getId());
        itemDto2.setComments(Collections.emptyList());

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> itemService.findAll(1L, 0, 0));

        Assertions.assertNotNull(exception.getParameter());
    }

    @Test
    void findAllTestWithLastAndNextBooking() {
        User user = new User(1L, "testName", "test@mail.com");
        User user2 = new User(2L, "testName2", "test@mail.com2");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        BookingShortDto bookingShortDtoLast = BookingShortDto.builder()
                .bookerId(user2.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 6, 28, 18, 0))
                .end(LocalDateTime.of(2023, 6, 28, 19, 0))
                .build();
        BookingShortDto bookingShortDtoNext = BookingShortDto.builder()
                .bookerId(user2.getId())
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, 6, 29, 18, 0))
                .end(LocalDateTime.of(2023, 6, 29, 19, 0))
                .build();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setLastBooking(bookingShortDtoLast);
        itemDto.setNextBooking(bookingShortDtoNext);
        itemDto.setComments(Collections.emptyList());

        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ItemMapper.toItem(itemDto))));

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        Mockito.when(bookingRepository.findAllByItemIdAndEndBeforeOrderByStartAsc(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(BookingMapper.toBooking(bookingShortDtoLast, item, user2)));

        Mockito.when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(BookingMapper.toBooking(bookingShortDtoNext, item, user2)));

        List<ItemDto> itemDtos = itemService.findAll(user.getId(), 0, 10);

        Assertions.assertEquals(itemDto, itemDtos.get(0));
    }

    @Test
    void updateWithDataNotFoundExceptionNotUserItem() {
        User user = new User(1L, "testName", "test@mail.com");
        User user2 = new User(2L, "testName2", "test@mail.com2");
        Item item = new Item(1L, "testName", "testDescription", true, user, null);
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user2));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.update(itemDto, itemDto.getId(), user2.getId()));

        Assertions.assertNotNull(exception.getParameter());
    }

    @Test
    void updateWithDataNotFoundException() {
        User user2 = new User(2L, "testName2", "test@mail.com2");
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user2));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> itemService.update(itemDto, itemDto.getId(), user2.getId()));

        Assertions.assertNotNull(exception.getParameter());
    }
}

