package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class DirectorDto {
        private Integer id;
        @NotBlank(message = "Название не может быть пустым.")
        private String name;
}