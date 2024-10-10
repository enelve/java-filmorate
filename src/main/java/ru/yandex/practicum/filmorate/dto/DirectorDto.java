package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;

public record DirectorDto(
        Integer id,
        @NotBlank(message = "Название не может быть пустым.")
        String name
) {
}