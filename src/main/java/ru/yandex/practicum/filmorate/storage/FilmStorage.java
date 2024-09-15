package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film remove(Integer id);

    Collection<Film> findAll();

    Optional<Film> findById(Integer id);

    boolean existsById(Integer id);
}
