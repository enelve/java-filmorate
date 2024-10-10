package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearch;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public Collection<FilmDto> getMostPopular(
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "genreId", required = false) Optional<Integer> genreId,
            @RequestParam(value = "year", required = false) Optional<Integer> year) {
        return filmService.getMostPopularByGenreAndYear(count, genreId, year).stream().map(FilmMapper::toDto).toList();
    }

    @DeleteMapping("{id}")
    public void deleteFilm(@PathVariable Integer id) {
        filmService.delete(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectors(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getDirectors(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<FilmSearch> searchFilms(
            @RequestParam(value = "query", defaultValue = "unknown") String query,
            @RequestParam(value = "by", defaultValue = "unknown") String by) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "friendId") Integer friendId) {
        return filmService.getCommonFilms(userId, friendId).stream()
                .map(FilmMapper::toDto)
                .toList();
    }
}
