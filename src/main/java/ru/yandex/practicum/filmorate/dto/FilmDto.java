package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.validator.ValidReleaseDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
public class FilmDto {
        private Integer id;
        @NotBlank(message = "Название не может быть пустым.")
        private String name;
        @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
        private String description;
        @ValidReleaseDate(message = "дата релиза — не раньше 28 декабря 1895 года.")
        private LocalDate releaseDate;
        @Positive(message = "Продолжительность фильма должна быть положительным числом.")
        private long duration;
        @NotNull
        @JsonProperty("mpa")
        private FilmRating filmRating;
        private Set<Genre> genres;
        private List<Director> directors;
}
