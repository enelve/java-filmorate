package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre getById(Integer id) {
        if (!genreRepository.exist(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        return genreRepository.getById(id);
    }

    public Collection<Genre> getAll() {
        return genreRepository.getAll();
    }
}
