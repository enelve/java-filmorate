package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Positive
    private Integer id;
    @Positive
    private String name;
}
