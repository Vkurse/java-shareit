package ru.practicum.shareit.exceptions;

public class BookingIsNotAvailableException extends RuntimeException {
    private final String parameter;

    public BookingIsNotAvailableException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
