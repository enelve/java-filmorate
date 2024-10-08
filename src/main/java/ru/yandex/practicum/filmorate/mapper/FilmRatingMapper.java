package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmRatingDto;
import ru.yandex.practicum.filmorate.model.FilmRating;

@UtilityClass
public class FilmRatingMapper {
    public static FilmRatingDto toDto(FilmRating filmRating) {
        return new FilmRatingDto(
                filmRating.getId(),
                filmRating.getName()
        );
    }

    public static FilmRating toEntity(FilmRatingDto filmRatingDto) {
        return new FilmRating(
                filmRatingDto.id(),
                filmRatingDto.name()
        );
    }
}
