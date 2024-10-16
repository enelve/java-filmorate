package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GenreDto {
    @NotNull
    private Integer id;
    @NotNull
    private String name;
}
