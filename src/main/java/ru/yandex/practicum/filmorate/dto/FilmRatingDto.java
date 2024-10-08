package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;

public record FilmRatingDto(@NotNull Integer id, @NotNull String name) {
}

