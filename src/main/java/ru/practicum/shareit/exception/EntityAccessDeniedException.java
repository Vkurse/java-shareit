package ru.practicum.shareit.exception;

public class EntityAccessDeniedException extends RuntimeException {
    public EntityAccessDeniedException(String s) {
        super(s);
    }
}