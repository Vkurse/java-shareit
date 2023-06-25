package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownBookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private ItemJpaRepository itemRepository;

    private Booking booking;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("itemDescription")
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();
    }

    @Test
    void addBooking() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookingDto.getBookerId())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingService.addBooking(1L, bookingDto))
                .thenReturn(BookingMapper.toBookingInfoDto(booking));

        assertThrows(InvalidEntityException.class, () -> {
            BookingDto exceptionBooking = BookingMapper.toBookingDto(booking);
            exceptionBooking.setStart(LocalDateTime.of(2050, Month.FEBRUARY, 25, 10, 0));
            bookingService.addBooking(1L, exceptionBooking);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            item.setOwner(user);
            bookingService.addBooking(1L, bookingDto);
        });

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(
                Item.builder()
                        .id(1L)
                        .name("item")
                        .description("itemDescription")
                        .available(false)
                        .owner(User.builder()
                                .id(2L)
                                .build())
                        .build()));

        assertThrows(InvalidEntityException.class, () -> {
            bookingService.addBooking(1L, bookingDto);
        });
    }

    @Test
    void updateBookingStatus() {
        item.setOwner(user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingService.updateBookingStatus(1L, 1L, true))
                .thenReturn(BookingMapper.toBookingInfoDto(booking));

        assertThrows(InvalidEntityException.class, () -> {
            booking.setStatus(BookingStatus.APPROVED);
            bookingService.updateBookingStatus(1L, 1L, true);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            item.setOwner(User.builder()
                    .id(2L)
                    .build());
            bookingService.updateBookingStatus(1L, 1L, true);
        });

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.updateBookingStatus(1L, 1L, true);
        });

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.updateBookingStatus(1L, 1L, true);
        });
    }

    @Test
    void getCurrentBooking() {
        Booking bookingTest = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, Month.MAY, 25, 12, 0))
                .end(LocalDateTime.of(2023, Month.MAY, 26, 12, 0))
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingTest));

        assertEquals(bookingService.getCurrentBooking(1L, 1L), BookingMapper.toBookingInfoDto(bookingTest));

        bookingTest.setBooker(User.builder()
                .id(10L)
                .build());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getCurrentBooking(1L, 1L);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getCurrentBooking(1L, 999L);
        });
    }

    @Test
    void getBooking() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "ALL", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "PAST", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "FUTURE", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "CURRENT", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "WAITING", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getBooking(user.getId(), "REJECTED", 0, 10), List.of(bookingInfoDto));

        assertThrows(UnknownBookingState.class, () -> {
            bookingService.getBooking(user.getId(), "TEST", 0, 10);
        });

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getBooking(user.getId(), "ALL", 0, 10);
        });
    }

    @Test
    void getOwnerBooking() {
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "ALL", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "PAST", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "FUTURE", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "CURRENT", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "WAITING", 0, 10), List.of(bookingInfoDto));

        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(booking));
        assertEquals(bookingService.getOwnerBooking(user.getId(), "REJECTED", 0, 10), List.of(bookingInfoDto));

        assertThrows(UnknownBookingState.class, () -> {
            bookingService.getOwnerBooking(user.getId(), "TEST", 0, 10);
        });

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getOwnerBooking(user.getId(), "ALL", 0, 10);
        });

    }
}