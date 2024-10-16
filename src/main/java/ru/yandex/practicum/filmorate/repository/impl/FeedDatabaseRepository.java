package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.mapper.FeedMapper;

import java.util.List;

@Repository
@Slf4j
@Component
@RequiredArgsConstructor
public class FeedDatabaseRepository implements FeedRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Integer userId, long entityId, EventTypesEnum eventTypes, OperationsEnum operations) {
        String sql = "INSERT INTO feed (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, eventTypes.toString(), operations.toString(), entityId);
    }

    @Override
    public List<Event> getFeed(Integer userId) {
        String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY time_stamp, event_id";
        return jdbcTemplate.query(sql, new FeedMapper(), userId);
    }
}
