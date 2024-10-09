package ru.yandex.practicum.filmorate.repository.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmDirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@Slf4j
@Component("FilmDatabaseRepository")
@RequiredArgsConstructor
public class FilmDatabaseRepository implements FilmRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDatabaseRepository directorDatabaseRepository;

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films (name, description, release_date, duration, film_rating_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getFilmRating().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        List<Director> directors = directorDatabaseRepository.addDirectorInFilm(film.getId(), film.getDirectors());
        film.setDirectors(directors);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, film_rating_id=? WHERE film_id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getFilmRating().getId(),
                film.getId()
        );
        Film filmFromDB = getById(film.getId());
        return filmFromDB;
    }

    @Override
    public Film delete(Integer id) {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        Collection<Film> films = jdbcTemplate.query(
                "SELECT film_id, name, description, release_date, duration, film_rating_id FROM films", new FilmMapper()
        );
        return films;
    }

    @Override
    public Film getById(Integer id) {
        Film filmFromDB = jdbcTemplate.queryForObject(
                "SELECT film_id, name, description, release_date, duration, film_rating_id FROM films WHERE film_id=?",
                new FilmMapper(),
                id
        );
        return filmFromDB;
    }

    @Override
    public void addGenres(Integer filmId, Set<Genre> genres) {
        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
        }
    }

    @Override
    public void deleteGenres(Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", filmId);
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        String sql;
        switch (sortBy) {
            case "year":
                sql = "SELECT f.*, d.director_id, d.director_name FROM film_director fd " +
                        "JOIN films f ON fd.film_id = f.film_id " +
                        "JOIN director d ON d.director_id = fd.director_id " +
                        "WHERE fd.director_id=? ORDER BY f.release_date";
                break;
            case "likes":
                  sql = "SELECT f.*, d.director_id, d.director_name, COUNT(l.USER_ID) FROM films f " +
                          "JOIN FILM_DIRECTOR fd ON fd.FILM_ID = f.FILM_ID  " +
                          "JOIN DIRECTOR d ON d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                          "JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                          "WHERE fd.director_id=? GROUP BY l.film_id " +
                          "ORDER BY COUNT (l.user_id) DESC";
                break;
            default:
                log.info("Запрашиваемой сортировки не существует: {}", sortBy);
                throw new ValidationException("Не корректный параметр сортировки");
        }
        return jdbcTemplate.query(sql, new FilmDirectorMapper(), directorId);
    }

    @Override
    public void updateGenres(Integer filmId, Set<Genre> genres) {
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    @Override
    public Set<Genre> getGenres(Integer filmId) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT f.genre_id, g.genre_type FROM film_genre AS f " +
                        "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id WHERE f.film_id=? ORDER BY g.genre_id ASC",
                new GenreMapper(), filmId);
        Set<Genre> result = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        result.addAll(genres);
        return result;
    }

    @Override
    public boolean exists(Integer id) {
        try {
            getById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.trace("there is no data for film with id = {}", id);
            return false;
        }
    }

}
