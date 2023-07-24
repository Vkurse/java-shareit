package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTests {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto userDto1;

    private BookingRequestDto bookingRequestDto;

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

        userDto1 = UserDto.builder()
                .name("name")
                .email("user1@email.com")
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.of(2022, 10, 24, 12, 30))
                .end(LocalDateTime.of(2023, 11, 10, 13, 0))
                .itemId(1L).build();
    }

    @Test
    void createTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        BookingResponseDto booking = bookingController.create(bookingRequestDto, user1.getId());
        assertEquals(1L, booking.getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(DataNotFoundException.class, () -> bookingController.create(bookingRequestDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        assertThrows(DataNotFoundException.class, () -> bookingController.create(bookingRequestDto, 1L));
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        assertThrows(DataNotFoundException.class, () -> bookingController.create(bookingRequestDto, 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        itemDto.setAvailable(false);
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        assertThrows(BookingIsNotAvailableException.class, () -> bookingController.create(bookingRequestDto, 2L));
    }

    @Test
    void createWithWrongEndDate() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        bookingRequestDto.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(BookingIsNotAvailableException.class, () -> bookingController.create(bookingRequestDto, user1.getId()));
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(DataNotFoundException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        BookingResponseDto booking = bookingController.create(bookingRequestDto, user1.getId());
        assertThrows(DataNotFoundException.class, () -> bookingController.approve(1L, 2L, true));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        BookingResponseDto booking = bookingController.create(bookingRequestDto, user1.getId());
        bookingController.approve(1L, 1L, true);
        assertThrows(BookingIsNotAvailableException.class, () -> bookingController.approve(1L, 1L, true));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        BookingResponseDto booking = bookingController.create(bookingRequestDto, user1.getId());
        assertEquals(1, bookingController.getAllByBooker(user1.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingController.getAllByBooker(user1.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByBooker(user1.getId(), "PAST", 0, 10).size());
        assertEquals(1, bookingController.getAllByBooker(user1.getId(), "CURRENT", 0, 10).size());
        assertEquals(0, bookingController.getAllByBooker(user1.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByBooker(user1.getId(), "REJECTED", 0, 10).size());
        bookingController.approve(booking.getId(), user.getId(), true);
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner(user.getId(), "ALL", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "WAITING", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "FUTURE", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "REJECTED", 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner(user.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(DataNotFoundException.class, () -> bookingController.getAllByBooker(1L, "ALL", 0, 10));
        assertThrows(DataNotFoundException.class, () -> bookingController.getAllByOwner(1L, "ALL", 0, 10));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(DataNotFoundException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        UserDto user = userController.create(UserMapper.toUser(userDto));
        ItemDto item = itemController.create(user.getId(), itemDto);
        UserDto user1 = userController.create(UserMapper.toUser(userDto1));
        BookingResponseDto booking = bookingController.create(bookingRequestDto, user1.getId());
        assertThrows(DataNotFoundException.class, () -> bookingController.getById(1L, 10L));
    }
}
