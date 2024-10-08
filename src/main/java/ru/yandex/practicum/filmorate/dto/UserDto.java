package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserDto(
        Integer id,
        @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
        String email,
        @NotBlank(message = "Логин не может быть пустым.")
        @Pattern(regexp = "\\s*\\S+\\s*", message = "Логин не может быть пустым.")
        String login,
        String name,
        @PastOrPresent(message = "Дата рождения не может быть в будущем")
        LocalDate birthday) {
}
