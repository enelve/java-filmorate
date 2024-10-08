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
                film.getGenres()
        );
    }

    public static Film toEntity(FilmDto filmDto) {
        return new Film(
                filmDto.id(),
                filmDto.name(),
                filmDto.description(),
                filmDto.releaseDate(),
                filmDto.duration(),
                filmDto.filmRating(),
                filmDto.genres()
        );
    }
}
