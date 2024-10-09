package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class FilmInMemoryRepository implements FilmRepository {
    private final Map<Integer, Film> filmMap;
    private final Map<Integer, Integer> likes;
    private Integer id;

    public FilmInMemoryRepository() {
        likes = new HashMap<>();
        filmMap = new HashMap<>();
        id = 0;
    }

    @Override
    public Film add(Film film) {
        film.setId(++id);
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Integer id) {
        filmMap.remove(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmMap.values();
    }

    @Override
    public Film getById(Integer id) {
        return filmMap.get(id);
    }

    @Override
    public boolean exists(Integer id) {
        return false;
    }

    @Override
    public void addGenres(Integer filmId, Set<Genre> genres) {
    }

    @Override
    public void updateGenres(Integer filmId, Set<Genre> genres) {
    }

    @Override
    public Set<Genre> getGenres(Integer filmId) {
        return null;
    }

    @Override
    public void deleteGenres(Integer filmId) {
    }
}
