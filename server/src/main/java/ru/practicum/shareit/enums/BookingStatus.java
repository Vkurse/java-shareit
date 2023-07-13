package ru.practicum.shareit.enums;

public enum BookingStatus {
    APPROVED("approved"),
    WAITING("waiting"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private String name;

    BookingStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}