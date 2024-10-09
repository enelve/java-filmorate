package ru.yandex.practicum.filmorate.repository.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserInMemoryRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Integer> friends = new HashMap<>();
    private int id = 0;

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Integer id) {
        users.remove(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        friends.put(id, friendId);
        return getById(id);
    }

    @Override
    public User removeFriend(Integer id, Integer friendId) {
        friends.remove(id);
        return getById(id);
    }

    @Override
    public boolean exists(Integer id) {
        return false;
    }
}
