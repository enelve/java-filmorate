package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmSearchMapper implements RowMapper<FilmSearch> {
    private final DirectorMapper directorMapper = new DirectorMapper();
    private final GenreMapper genreMapper = new GenreMapper();

    @Override
    public FilmSearch mapRow(ResultSet rs, int rowNum) throws SQLException {

        FilmSearch film = new FilmSearch();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));
        FilmRating filmRating = new FilmRating();
        filmRating.setId(rs.getInt("film_rating_id"));
        filmRating.setName(rs.getString("rating_value"));
        film.setMpa(filmRating);
        List<Director> directors = new ArrayList<>();
        int directorId = rs.getInt("director_id");
        if (directorId != 0) {
            Director director = directorMapper.mapRow(rs, rowNum);
            directors.add(director);
        }
        film.setDirectors(directors);
        Set<Genre> genres = new HashSet<>();
        Genre genre = genreMapper.mapRow(rs, rowNum);
        genres.add(genre);
        film.setGenres(genres);
        return film;
    }
}