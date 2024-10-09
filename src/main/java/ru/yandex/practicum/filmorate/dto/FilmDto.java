package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record FilmDto(
        Integer id,
        @NotBlank(message = "Название не может быть пустым.")
        String name,
        @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
        String description,
        @ValidReleaseDate(message = "дата релиза — не раньше 28 декабря 1895 года.")
        LocalDate releaseDate,
        @Positive(message = "Продолжительность фильма должна быть положительным числом.")
        long duration,
        @NotNull
        @JsonProperty("mpa")
        FilmRating filmRating,
        Set<Genre> genres,
        List<Director> directors
) {
}
