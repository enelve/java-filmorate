package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.repository.FilmRatingRepository;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmRatingService {
    private final FilmRatingRepository filmRatingRepository;

    public FilmRating getById(Integer id) {
        if (!filmRatingRepository.exist(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        return filmRatingRepository.getById(id);
    }

    public Collection<FilmRating> getAll() {
        return filmRatingRepository.getAll();
    }
}
