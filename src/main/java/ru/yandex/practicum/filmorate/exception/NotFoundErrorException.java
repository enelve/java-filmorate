package ru.yandex.practicum.filmorate.exception;

public class NotFoundErrorException extends RuntimeException {
    public NotFoundErrorException(String message) {
        super(message);
    }
}