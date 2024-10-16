package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmDirectorMapper implements RowMapper<Film> {
    private final DirectorMapper directorMapper = new DirectorMapper();

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));
        FilmRating filmRating = new FilmRating();
        filmRating.setId(rs.getInt("film_rating_id"));
        film.setFilmRating(filmRating);
        List<Director> directors = new ArrayList<>();
        Director director = directorMapper.mapRow(rs, rowNum);
        directors.add(director);
        film.setDirectors(directors);
        return film;
    }
}