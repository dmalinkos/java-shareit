package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CustomAdvice {

    /**
     * Обрабатывает исключение отсутсвия сущности
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotExistException(EntityNotExistException e) {
        return Map.of("error", e.getMessage());
    }

    /**
     * Обрабатывает исключение дублирования данных
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateConflictException(DuplicateConflictException e) {
        return Map.of("error", e.getMessage());
    }

    /**
     * Обрабатывает исключение некорректного владельца
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleIncorrectOwnerException(IncorrectOwnerException e) {
        return Map.of("error", e.getMessage());
    }

}