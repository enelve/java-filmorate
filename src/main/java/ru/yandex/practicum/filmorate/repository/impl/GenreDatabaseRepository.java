package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
@Component
@RequiredArgsConstructor
public class GenreDatabaseRepository implements GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getById(Integer id) {
        Genre genre = jdbcTemplate.queryForObject("SELECT genre_id, genre_type FROM genre WHERE genre_id=?", new GenreMapper(), id);
        return genre;
    }

    @Override
    public Collection<Genre> getAll() {
        Collection<Genre> genres = jdbcTemplate.query("SELECT genre_id, genre_type FROM genre ORDER BY genre_id", new GenreMapper());
        return genres;
    }

    @Override
    public boolean exist(Integer id) {
        try {
            getById(id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }
}
