package ru.practicum.shareit.exceptions;

public class DataNotFoundException extends RuntimeException {
    private final String parameter;

    public DataNotFoundException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
