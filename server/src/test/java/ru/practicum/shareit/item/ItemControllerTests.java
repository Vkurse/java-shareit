package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemRequest.ItemRequestController;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTests {
    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemDto itemDto;

    private UserDto userDto;

    private ItemRequestDto itemRequestDto;

    private CommentDto comment;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .description("item request description")
                .build();

        comment = CommentDto
                .builder()
                .text("first comment")
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(1L, itemDto);
        assertEquals(item.getId(), itemController.findById(item.getId(), user.getId()).getId());
    }

    @Test
    void createWithRequestTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemRequestResponseDto itemRequest = itemRequestController.create(user.getId(), itemRequestDto);
        itemDto.setRequestId(1L);
        UserDto user2 = userController.create(UserMapper.toUser(userDto.toBuilder().email("user2@email.com").build()));
        ItemDto item = itemController.create(2L, itemDto);
        item.setComments(Collections.emptyList());
        assertEquals(item, itemController.findById(1L, 2L));
    }

    @Test
    void createByWrongUser() {
        assertThrows(DataNotFoundException.class, () -> itemController.create(1L, itemDto));
    }

    @Test
    void createWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        UserDto user = userController.create(UserMapper.toUser(userDto));
        assertThrows(DataNotFoundException.class, () -> itemController.create(1L, itemDto));
    }

    @Test
    void updateTest() {
        userController.create(UserMapper.toUser(userDto));
        itemController.create(1L, itemDto);
        ItemDto item = itemDto.toBuilder().name("new name").description("updateDescription").available(false).build();
        itemController.update(item, 1L, 1L);
        assertEquals(item.getDescription(), itemController.findById(1L, 1L).getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(DataNotFoundException.class, () -> itemController.update(itemDto, 1L, 1L));
    }

    @Test
    void updateByWrongUserTest() {
        userController.create(UserMapper.toUser(userDto));
        itemController.create(1L, itemDto);
        assertThrows(DataNotFoundException.class, () -> itemController.update(itemDto.toBuilder()
                .name("new name").build(), 1L, 10L));
    }

    @Test
    void searchTest() {
        userController.create(UserMapper.toUser(userDto));
        itemController.create(1L, itemDto);
        assertEquals(1, itemController.search("Desc", 0, 10).size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.create(UserMapper.toUser(userDto));
        itemController.create(1L, itemDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.search("", 0, 10));
    }

    @Test
    void searchWithWrongFrom() {
        assertThrows(BadRequestException.class, () -> itemController.search("t", -1, 10));
    }

    @Test
    void createCommentTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(1L, itemDto);
        UserDto user2 = userController.create(UserMapper.toUser(userDto.toBuilder().email("email2@mail.com").build()));
        bookingController.create(BookingRequestDto.builder()
                .start(LocalDateTime.of(2022, 10, 20, 12, 15))
                .end(LocalDateTime.of(2022, 10, 27, 12, 15))
                .itemId(item.getId()).build(), user2.getId());
        bookingController.approve(1L, 1L, true);
        itemController.addComment(item.getId(), user2.getId(), comment);
        assertEquals(1, itemController.findById(1L, 1L).getComments().size());
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(DataNotFoundException.class, () -> itemController.addComment(1L, 1L, comment));
    }

    @Test
    void createCommentToWrongItem() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        assertThrows(DataNotFoundException.class, () -> itemController.addComment(1L, 1L, comment));
        ItemDto item = itemController.create(1L, itemDto);
        assertThrows(BookingIsNotAvailableException.class, () -> itemController.addComment(1L, 1L, comment));
    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(BadRequestException.class, () -> itemController.findAll(1L, -1, 10));
    }
}
