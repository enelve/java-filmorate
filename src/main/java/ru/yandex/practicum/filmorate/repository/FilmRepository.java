package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface FilmRepository {
    Film add(Film film);

    Film update(Film film);

    void delete(Integer id);

    Collection<Film> getAll();

    Film getById(Integer id);

    boolean exists(Integer id);

    void addGenres(Integer filmId, Set<Genre> genres);

    void updateGenres(Integer filmId, Set<Genre> genres);

    Set<Genre> getGenres(Integer filmId);

    void deleteGenres(Integer filmId);
}
