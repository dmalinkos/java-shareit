package ru.practicum.shareit.exception;

public class UnavailableError extends RuntimeException {
    public UnavailableError(String msg) {
        super(msg);
    }
}
