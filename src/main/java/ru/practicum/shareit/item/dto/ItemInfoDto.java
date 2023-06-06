package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemInfoDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}