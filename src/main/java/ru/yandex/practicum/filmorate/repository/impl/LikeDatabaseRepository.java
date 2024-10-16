package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.mapper.LikeMapper;

@Repository
@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDatabaseRepository implements LikeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Integer filmId, Integer userId) {
        jdbcTemplate.update("MERGE INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void remove(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public int getCount(Integer filmId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id=?", Integer.class, filmId);
        return count;
    }

    @Override
    public boolean isFilmLikedByUser(Integer filmId, Integer userId) {
        try {
            jdbcTemplate.queryForObject("SELECT film_id, user_id FROM likes WHERE film_id=? AND user_id=?",
                    new LikeMapper(), filmId, userId);
            log.trace("Фильм {} пролайкан пользователем {}", filmId, userId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Фильм {} еще не успел понравиться пользователю {}", filmId, userId);
            return false;
        }
    }
}
