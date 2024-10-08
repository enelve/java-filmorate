package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreRepository {
    Genre getById(Integer id);

    Collection<Genre> getAll();

    boolean exist(Integer id);
}
