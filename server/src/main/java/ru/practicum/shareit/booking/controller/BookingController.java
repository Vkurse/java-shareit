package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USERID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;


    @PostMapping
    public BookingInfoDto addBooking(@RequestHeader(USERID_HEADER) Long userId, @RequestBody BookingDto booking) {
        return bookingService.addBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto updateBookingStatus(@RequestHeader(USERID_HEADER) Long userId,
                                              @PathVariable Long bookingId,
                                              @RequestParam Boolean approved) {

        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingInfoDto getCurrentBooking(@RequestHeader(USERID_HEADER) Long userId,
                                            @PathVariable Long bookingId) {
        return bookingService.getCurrentBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingInfoDto> getBooking(@RequestHeader(USERID_HEADER) Long userId,
                                           @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {

        return bookingService.getBooking(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getOwnerBooking(@RequestHeader(USERID_HEADER) Long userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {

        return bookingService.getOwnerBooking(userId, stateParam, from, size);
    }
}
