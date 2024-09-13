package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User add(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User remove(Integer id) {
        return users.remove(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(Integer id) {
        return users.containsKey(id);
    }

    private int generateId() {
        return ++id;
    }
}
