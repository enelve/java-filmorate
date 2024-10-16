package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
        private Integer id;
        @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
        private String email;
        @NotBlank(message = "Логин не может быть пустым.")
        @Pattern(regexp = "\\s*\\S+\\s*", message = "Логин не может быть пустым.")
        private String login;
        private String name;
        @PastOrPresent(message = "Дата рождения не может быть в будущем")
        private LocalDate birthday;
}
