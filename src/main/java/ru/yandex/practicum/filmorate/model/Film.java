package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;
    @ValidReleaseDate(message = "дата релиза — не раньше 28 декабря 1895 года.")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private long duration;
    private final Set<Integer> likesUserId = new HashSet<>();
}
