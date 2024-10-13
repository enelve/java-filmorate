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
import ru.yandex.practicum.filmorate.repository.mapper.FilmDirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmSearchMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearch;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.mapper.FilmMpaDirectorMapper;
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
    public void delete(Integer id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id=?", id);
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
//                sql = "SELECT f.*, fr.RATING_VALUE, d.director_id, d.director_name, g.GENRE_ID, g.GENRE_TYPE FROM film_director fd " +
//                        "JOIN films f ON fd.film_id = f.film_id " +
//                        "JOIN director d ON d.director_id = fd.director_id " +
//                        "JOIN FILM_GENRE fg ON fg.FILM_ID = f.FILM_ID " +
//                        "JOIN genre g ON g.genre_id = fg.GENRE_ID " +
//                        "JOIN FILM_RATING fr ON fr.FILM_RATING_ID = f.FILM_RATING_ID " +
//                        "WHERE fd.director_id=? ORDER BY f.release_date";
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
        return jdbcTemplate.query(sql, new FilmMapper(), directorId);
    }

    @Override
    public List<FilmSearch> getFilmBySearch(String query, String by) {
        StringBuilder sql = new StringBuilder("SELECT f.*, d.*, g.*, fr.RATING_VALUE "
            + "FROM films f "
            + "LEFT JOIN likes ml ON f.film_id = ml.film_id "
            + "LEFT JOIN film_genre fg ON fg.FILM_ID = f.FILM_ID "
            + "LEFT JOIN genre g ON g.genre_id = fg.GENRE_ID  "
            + "LEFT JOIN film_director fd ON f.film_id = fd.film_id "
            + "LEFT JOIN film_rating fr ON fr.film_rating_id = f.film_id "
            + "LEFT JOIN director d ON fd.director_id = d.director_id ");

        if (by.equals("title")) {
            sql.append("WHERE LOWER(f.name) LIKE LOWER('%").append(query).append("%') ");
        }
        if (by.equals("director")) {
            sql.append("WHERE LOWER(d.director_name) LIKE LOWER('%").append(query).append("%') ");
        }
        if (by.equals("title,director") || by.equals("director,title")) {
            sql.append("WHERE LOWER(f.name) LIKE LOWER('%").append(query).append("%') ");
            sql.append("OR LOWER(d.director_name) LIKE LOWER('%").append(query).append("%') ");
        }
        sql.append("GROUP BY f.film_id, ml.film_id " + "ORDER BY COUNT(ml.film_id) DESC");

        return jdbcTemplate.query(sql.toString(), new FilmSearchMapper());
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
