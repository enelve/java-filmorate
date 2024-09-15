package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0001;
import static ru.yandex.practicum.filmorate.exception.Error.ERROR_0002;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film add(Film film) {
        Integer filmId = film.getId();
        if (filmStorage.existsById(filmId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), filmId));
        }
        log.info("Добавлена информация о новом фильме: {}", film.getName());
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        Integer filmId = film.getId();
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), filmId));
        }
        filmStorage.update(film);
        log.info("Изменена информация о пользователе: : {}:{}", filmId, film.getName());
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ERROR_0001.message(), id)));
    }

    public Film addLike(Integer filmId, Integer userId) {
        final User user = userService.getUserById(userId);
        final Film film = filmStorage.findById(filmId).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), filmId)));
        film.getLikesUserId().add(user.getId());
        filmStorage.update(film);

        log.info("Пользователю {} понравился фильм {}", user.getName(), film.getName());
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        final User user = userService.getUserById(userId);
        final Film film = filmStorage.findById(filmId).orElseThrow(() ->
                new NotFoundException(String.format(ERROR_0001.message(), filmId)));
        Set<Integer> likes = film.getLikesUserId();

        if (!likes.contains(userId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), filmId));
        }

        likes.remove(user.getId());
        filmStorage.update(film);

        log.info("Пользователю {} разонравился фильм {}", user.getName(), film.getName());
        return film;
    }

    public List<Film> getMostPopular(Integer count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> o2.getLikesUserId().size() - o1.getLikesUserId().size())
                .limit(count)
                .toList();
    }
}
