package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmMapper {
    public static FilmDto toDto(Film film) {
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getFilmRating(),
                film.getGenres(),
                film.getDirectors()
        );
    }

    public static Film toEntity(FilmDto filmDto) {
        return new Film(
                filmDto.getId(),
                filmDto.getName(),
                filmDto.getDescription(),
                filmDto.getReleaseDate(),
                filmDto.getDuration(),
                filmDto.getFilmRating(),
                filmDto.getGenres(),
                filmDto.getDirectors()
        );
    }
}
