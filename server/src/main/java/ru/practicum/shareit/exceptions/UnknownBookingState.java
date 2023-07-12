package ru.practicum.shareit.exceptions;

public class UnknownBookingState extends RuntimeException {
    public UnknownBookingState(String message) {
        super(message);
    }
}