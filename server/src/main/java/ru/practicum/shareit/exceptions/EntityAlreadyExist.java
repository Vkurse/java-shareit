package ru.practicum.shareit.exceptions;

public class EntityAlreadyExist extends RuntimeException {

    public EntityAlreadyExist(String message) {
        super(message);
    }
}