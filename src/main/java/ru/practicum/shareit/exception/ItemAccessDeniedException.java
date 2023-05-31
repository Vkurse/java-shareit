package ru.practicum.shareit.exception;

public class ItemAccessDeniedException extends RuntimeException {
    public ItemAccessDeniedException(String s) {
        super(s);
    }
}