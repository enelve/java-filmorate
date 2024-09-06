package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController()
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping()
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        user.setId(generateId());
        user.setName(Optional.ofNullable(user.getName()).orElse(user.getLogin()));
        users.put(user.getId(), user);
        log.info("Добавлена информация о новом пользователе: {}", user.getName());
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Изменена информация о пользователе: {}", user.getName());
        } else {
            throw new NotFoundException("Не существующий пользователь.");
        }
        return user;
    }

    private int generateId() {
        return id++;
    }
}
