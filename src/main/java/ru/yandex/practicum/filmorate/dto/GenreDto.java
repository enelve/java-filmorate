package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;

public record GenreDto(@NotNull Integer id, @NotNull String name) {
}
