package ru.practicum.shareit.exception;

public class EntityNotExistException extends RuntimeException {

    public EntityNotExistException(String msg) {
        super(msg);
    }

}
