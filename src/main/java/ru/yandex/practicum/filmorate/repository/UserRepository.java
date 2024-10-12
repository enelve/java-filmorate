package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserRepository {
    User add(User user);

    User update(User user);

    void delete(Integer id);

    Collection<User> getAll();

    User getById(Integer id);

    User addFriend(Integer id, Integer friendId);

    User removeFriend(Integer id, Integer friendId);

    boolean exists(Integer id);

    Collection<Film> getRecommendations(Integer userId);
}
