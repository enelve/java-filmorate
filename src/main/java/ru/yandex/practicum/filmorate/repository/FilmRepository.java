package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearch;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
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

    List<Film> getDirectorFilms(int directorId, String sortBy);

    List<FilmSearch> getFilmBySearch(String query, String by);
}
