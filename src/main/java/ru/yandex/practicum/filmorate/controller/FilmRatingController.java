package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmRatingDto;
import ru.yandex.practicum.filmorate.mapper.FilmRatingMapper;
import ru.yandex.practicum.filmorate.service.FilmRatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class FilmRatingController {
    private final FilmRatingService filmRatingService;

    @GetMapping("/{id}")
    public FilmRatingDto getFilmRatingById(@PathVariable Integer id) {
        return FilmRatingMapper.toDto(filmRatingService.getById(id));
    }

    @GetMapping
    public List<FilmRatingDto> getFilmRatingList() {
        return filmRatingService.getAll().stream().map(FilmRatingMapper::toDto).toList();
    }
}
