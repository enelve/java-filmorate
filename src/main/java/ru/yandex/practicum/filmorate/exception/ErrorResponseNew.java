package ru.yandex.practicum.filmorate.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseNew {
    private int status;
    private String error;
    private LocalDateTime timestamp;
}
