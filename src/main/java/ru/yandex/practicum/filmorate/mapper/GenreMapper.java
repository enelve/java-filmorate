package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@UtilityClass
public class GenreMapper {
    public static GenreDto toDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }

    public static Genre toEntity(GenreDto genreDto) {
        return new Genre(
                genreDto.id(),
                genreDto.name()
        );
    }
}
