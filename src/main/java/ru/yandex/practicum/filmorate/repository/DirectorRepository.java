package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;


public interface DirectorRepository {
    Collection<Director> findAll();

    Director findById(Integer id);

    Director save(Director director);

    Director update(Director director);

    List<Director> getDirectorListFromFilm(Integer filmId);

    List<Director> addDirectorInFilm(Integer filmId, List<Director> directorList);

    boolean exists(Integer id);

    void delete(Integer id);

    void deleteFromFilm(Integer filmId);
}
