package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingDto, Long userId);

    BookingResponseDto approve(Long bookingId, Long ownerId, Boolean approve);

    BookingResponseDto getById(Long bookingId, Long userId);

    List<BookingResponseDto> getAllByOwner(Long userId, String state, int from, int size);

    List<BookingResponseDto> getAllByBooker(Long userId, String state, int from, int size);
}
