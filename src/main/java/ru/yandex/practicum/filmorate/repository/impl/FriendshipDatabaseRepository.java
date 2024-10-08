package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.repository.FriendshipRepository;
import ru.yandex.practicum.filmorate.repository.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDatabaseRepository implements FriendshipRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId, boolean isFriend) {
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, is_friend) VALUES(?, ?, ?)", userId, friendId, isFriend);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    @Override
    public Collection<Integer> getFriends(Integer userId) {
        Collection<Integer> friends = jdbcTemplate.query("SELECT user_id, friend_id, is_friend FROM friends WHERE user_id=?", new FriendshipMapper(), userId).stream().map(Friendship::getFriendId).collect(Collectors.toList());
        return friends;
    }

    @Override
    public Friendship getFriend(Integer userId, Integer friendId) {
        return jdbcTemplate.queryForObject("SELECT user_id, friend_id, is_friend FROM friends WHERE user_id=? AND friend_id=?", new FriendshipMapper(), userId, friendId);
    }

    @Override
    public boolean exist(Integer userId, Integer friendId) {
        try {
            getFriend(userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Дружба не зарегистрированна между {} и {}", userId, friendId);
            return false;
        }
    }
}
