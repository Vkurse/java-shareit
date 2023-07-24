package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

class BookingServiceImplTest {
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;
    BookingService bookingService;

    @BeforeEach
    void beforeEach() {
        userService = Mockito.mock(UserService.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userService,
                itemRepository
        );
    }

    @Test
    void createTestWithDataNotFoundException() {
        User user = new User(1L, "testName", "test@mail.com");
        Item item = new Item(1L, "testName", "testDescription", false, user, null);
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.create(bookingRequestDto, user.getId()));

        Assertions.assertNotNull(exception.getParameter());
    }

    @Test
    void createTestWithBookingIsNotAvailableException() {
        User user = new User(1L, "testName", "test@mail.com");
        User user2 = new User(2L, "testName2", "testEmail2@mail.com");
        Item item = new Item(1L, "testName", "testDescription", false, user, null);
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(user));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        BookingIsNotAvailableException exception = Assertions.assertThrows(BookingIsNotAvailableException.class,
                () -> bookingService.create(bookingRequestDto, user2.getId()));

        Assertions.assertNotNull(exception.getParameter());
    }

    @Test
    void getByIdWithDataNotFoundException() {
        User user = new User(1L, "testName", "test@mail.com");
        User user2 = new User(2L, "testName2", "testEmail2@mail.com");
        Item item = new Item(1L, "testName", "testDescription", false, user, null);
        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        DataNotFoundException exception = Assertions.assertThrows(DataNotFoundException.class,
                () -> bookingService.getById(booking.getId(), user2.getId()));

        Assertions.assertNotNull(exception.getParameter());
    }
}