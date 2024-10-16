package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.repository.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDatabaseRepository implements DirectorRepository {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Collection<Director> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM director", new DirectorMapper()
        );
    }

    @Override
    public Director findById(Integer id) {
        Director directorDB = jdbcTemplate.queryForObject(
                "SELECT * FROM director WHERE director_id=?",
                new DirectorMapper(),
                id
        );
        return directorDB;
    }

    @Override
    public Director save(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO director (director_name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        jdbcTemplate.update(
                "UPDATE director SET director_name=? WHERE director_id=?",
                director.getName(),
                director.getId()
        );
        return findById(director.getId());
    }

    @Override
    public List<Director> getDirectorListFromFilm(Integer filmId) {
        String sql = "SELECT fd.*, d.director_name FROM film_director AS fd JOIN director AS d ON d.director_id = fd.director_id WHERE fd.film_id=?";
        return jdbcTemplate.query(sql, new DirectorMapper(), filmId);
    }

    @Override
    public List<Director> addDirectorInFilm(Integer filmId, List<Director> directorList) {
        String sql = "MERGE INTO film_director (film_id, director_id) KEY(film_id, director_id) VALUES (?, ?)";
        if (directorList == null || directorList.isEmpty()) {
            log.info("Режиссер еще не был добавлен в базу данных");
            return new ArrayList<>();
        }

        for (Director director : directorList) {
            jdbcTemplate.update(sql, filmId, director.getId());
        }

        return getDirectorListFromFilm(filmId);
    }

    @Override
    public boolean exists(Integer id) {
        try {
            findById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.trace("there is no data for film with id = {}", id);
            return false;
        }
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update(
                "DELETE FROM director WHERE director_id=?",
                id
        );
    }

    @Override
    public void deleteFromFilm(Integer filmId) {
        jdbcTemplate.update(
                "DELETE FROM film_director WHERE film_id=?",
                filmId
        );
    }
}
