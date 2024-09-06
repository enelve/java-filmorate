package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int id = 0;

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлена информация о новом фильме: {}", film.getName());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("Не существующий фильм.");
        }
        log.info("Изменена информация о фильме: {}", film.getName());
        return film;
    }

    private int generateId() {
        return id++;
    }
}