package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRatingMapper implements RowMapper<FilmRating> {

    @Override
    public FilmRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmRating filmRating = new FilmRating();
        filmRating.setId(rs.getInt("film_rating_id"));
        filmRating.setName(rs.getString("rating_value"));
        return filmRating;
    }
}
