package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> returnAll() {
        return filmService.getAll().stream().map(FilmMapper::toDto).toList();
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody FilmDto filmDto) {
        Film film = FilmMapper.toEntity(filmDto);
        return FilmMapper.toDto(filmService.add(film));
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto filmDto) {
        Film film = FilmMapper.toEntity(filmDto);
        return FilmMapper.toDto(filmService.update(film));
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        return FilmMapper.toDto(filmService.getById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return FilmMapper.toDto(filmService.addLike(id, userId));
    }

    @DeleteMapping("{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return FilmMapper.toDto(filmService.deleteLike(id, userId));
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopular(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getMostPopular(count).stream().map(FilmMapper::toDto).toList();
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        filmService.delete(id);
    }
}
