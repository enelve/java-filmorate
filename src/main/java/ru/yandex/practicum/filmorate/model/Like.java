package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Positive
    private Long filmId;
    @Positive
    private Long userId;
}
