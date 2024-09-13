package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Фильм с Id = {} уже есть.", film.getId());
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
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
            throw new HttpClientErrorException(HttpStatus.NO_CONTENT);
        }
        log.info("Изменена информация о фильме: {}", film.getName());
        return film;
    }

    private int generateId() {
        return ++id;
    }
}