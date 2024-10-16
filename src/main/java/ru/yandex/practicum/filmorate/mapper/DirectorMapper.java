package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@UtilityClass
public class DirectorMapper {
    public static DirectorDto toDto(Director director) {
        return new DirectorDto(
                director.getId(),
                director.getName()
        );
    }

    public static Director toEntity(DirectorDto directorDto) {
        return new Director(
                directorDto.getId(),
                directorDto.getName()
        );
    }
}
