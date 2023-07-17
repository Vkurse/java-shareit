package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
}
