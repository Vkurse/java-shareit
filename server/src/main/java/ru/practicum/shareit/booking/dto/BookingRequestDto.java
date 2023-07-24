package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}

