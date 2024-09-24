package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.FilmRatingRepository;
import ru.yandex.practicum.filmorate.repository.mapper.FilmRatingMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.Collection;

@Repository
@Slf4j
@Component
@RequiredArgsConstructor
public class FilmRatingDatabaseRepository implements FilmRatingRepository {
    private final JdbcTemplate jdbcTemplate;

    public FilmRating getById(Integer id) {
        FilmRating filmRating = jdbcTemplate.queryForObject(
                "SELECT film_rating_id, rating_value FROM film_rating WHERE film_rating_id=?",
                new FilmRatingMapper(), id);
        return filmRating;
    }

    public Collection<FilmRating> getAll() {
        Collection<FilmRating> filmRatingCollection = jdbcTemplate.query(
                "SELECT film_rating_id, rating_value FROM film_rating ORDER BY film_rating_id",
                new FilmRatingMapper());
        return filmRatingCollection;
    }

    @Override
    public boolean exist(Integer id) {
        try {
            getById(id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.warn("Категории с id {} нет в БД", id);
            return false;
        }
    }
}
