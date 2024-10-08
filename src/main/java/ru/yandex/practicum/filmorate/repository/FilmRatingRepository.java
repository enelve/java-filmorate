package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.Collection;

public interface FilmRatingRepository {
    FilmRating getById(Integer id);

    Collection<FilmRating> getAll();

    boolean exist(Integer id);

}
