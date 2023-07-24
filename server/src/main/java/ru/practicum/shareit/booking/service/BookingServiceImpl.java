package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingResponseDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingResponseDto create(BookingRequestDto bookingDto, Long userId) {
        User user = toUser(userService.getById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new DataNotFoundException("Предмет не найден"));
        if (userId.equals(item.getOwner().getId())) {
            log.error("Невозможно создать бронирование своей же ввещи");
            throw new DataNotFoundException("Невозможно создать бронирование своей же ввещи");
        }
        if (!item.getAvailable()) {
            log.error("Данная вещь недоступна");
            throw new BookingIsNotAvailableException("Данная вещь недоступна");
        }
        Booking booking = toBooking(bookingDto, item, user);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            log.error("Дата окончания бронирования не может быть больше даты начала или равна 0");
            throw new BookingIsNotAvailableException("Дата окончания бронирования не может быть больше даты начала или равна 0");
        }
        booking.setStatus(Status.WAITING);
        Booking responseBooking = bookingRepository.save(booking);
        return toBookingResponseDto(responseBooking);
    }

    @Override
    public BookingResponseDto approve(Long bookingId, Long ownerId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new DataNotFoundException("Бронирование не найдено"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            log.error("Бронирование у пользователя с id {} не найдено", ownerId);
            throw new DataNotFoundException(String.format("Бронирование у пользователя с id %d не найдено", ownerId));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            log.error("Бронирование уже подтверждено или отклонено");
            throw new BookingIsNotAvailableException("Бронирование уже подтверждено или отклонено");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking responseBooking = bookingRepository.save(booking);
        return toBookingResponseDto(responseBooking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {
        Booking bookings = bookingRepository.findById(bookingId).orElseThrow(() ->
                new DataNotFoundException("Бронирование не найдено"));
        if (!userId.equals(bookings.getBooker().getId()) && !userId.equals(bookings.getItem().getOwner().getId())) {
            log.error("Вы не владелец или автор бронирования");
            throw new DataNotFoundException("Вы не владелец или автор бронирования");
        }
        return toBookingResponseDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllByOwner(Long userId, String state, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Не правильно переданы параметры поиска, индекс первого элемента не может" +
                    " быть меньше нуля а размер страницы должен быть больше нуля");
        }
        final Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                sort
        );
        User user = toUser(userService.getById(userId));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByItemOwner(user, pageable));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), pageable));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageable));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, pageable));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, pageable));
                break;
        }
        return toBookingResponseDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllByBooker(Long userId, String state, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Не правильно переданы параметры поиска, индекс первого элемента не может" +
                    " быть меньше нуля а размер страницы должен быть больше нуля");
        }
        final Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                sort
        );
        User user = toUser(userService.getById(userId));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBooker(user, pageable).toList());
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable).toList());
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), pageable)
                        .toList());
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable)
                        .toList());
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, pageable)
                        .toList());
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, pageable)
                        .toList());
                break;
        }
        return toBookingResponseDto(bookings);
    }
}
