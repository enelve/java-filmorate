package ru.yandex.practicum.filmorate.exception;

import java.time.LocalDateTime;

public record ErrorResponseNew(int status, String error, LocalDateTime timestamp){

}
