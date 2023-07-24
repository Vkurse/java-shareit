package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел POST запрос на добавление новой аренды {} от пользователя с id {}", bookingDto, userId);
        BookingResponseDto booking = bookingService.create(bookingDto, userId);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam Boolean approved) {
        log.info("Пришел /POST запрос на принятие или отклонение аренды от пользователя с id {} к предмету с id {}",
                userId, bookingId);
        BookingResponseDto booking = bookingService.approve(bookingId, userId, approved);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Пришел /GET запрос на получение данных об аренде с id {} от пользователя {}", bookingId, userId);
        BookingResponseDto booking = bookingService.getById(bookingId, userId);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для владельца с id {}, и с параметром {}",
                userId, state);
        List<BookingResponseDto> bookings = bookingService.getAllByOwner(userId, state, from, size);
        log.info("Ответ отправлен {}", bookings);
        return bookings;
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для пользователя с id {}, и с параметром {}",
                userId, state);
        List<BookingResponseDto> bookings = bookingService.getAllByBooker(userId, state, from, size);
        log.info("Ответ отправлен {}", bookings);
        return bookings;
    }
}
