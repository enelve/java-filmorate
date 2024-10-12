package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventTypesEnum;
import ru.yandex.practicum.filmorate.enums.OperationsEnum;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotContentException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSearch;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.yandex.practicum.filmorate.exception.Error.*;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmRatingRepository filmRatingRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final FeedRepository feedRepository;
    private static final EventTypesEnum EVENT_TYPES = EventTypesEnum.LIKE;
    @Autowired
    public FilmService(@Qualifier("FilmDatabaseRepository") FilmRepository filmRepository, FilmRatingRepository filmRatingRepository,
                       LikeRepository likeRepository, GenreRepository genreRepository, DirectorRepository directorRepository, FeedRepository feedRepository) {
        this.filmRepository = filmRepository;
        this.filmRatingRepository = filmRatingRepository;
        this.likeRepository = likeRepository;
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
        this.feedRepository = feedRepository;
    }

    public Film add(Film film) {
        Integer filmId = film.getId();
        if (filmRepository.exists(filmId)) {
            throw new DuplicateException(String.format(ERROR_0002.message(), filmId));
        }
        if (!filmRatingRepository.exist(film.getFilmRating().getId())) {
            throw new DuplicateException(String.format(ERROR_0002.message(), filmId));
        }

        Film newFilm = filmRepository.add(film);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!genreRepository.exist(genre.getId())) {
                    throw new DuplicateException(String.format(ERROR_0002.message(), filmId));
                }
            }
            filmRepository.addGenres(newFilm.getId(), film.getGenres());
            newFilm.setGenres(filmRepository.getGenres(newFilm.getId()));
        }
        newFilm.setFilmRating(filmRatingRepository.getById(newFilm.getFilmRating().getId()));
        log.info("Добавлена информация о новом фильме: {}", film.getName());
        return newFilm;
    }

    public Film update(Film film) {
        Integer filmId = film.getId();
        if (!filmRepository.exists(filmId)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), filmId));
        }
        Film newFilm = filmRepository.update(film);
        if (film.getGenres() != null) {
            filmRepository.updateGenres(newFilm.getId(), film.getGenres());
        }
        newFilm.setGenres(filmRepository.getGenres(newFilm.getId()));
        newFilm.setFilmRating(filmRatingRepository.getById(newFilm.getFilmRating().getId()));
        newFilm.setDirectors(directorRepository.addDirectorInFilm(newFilm.getId(), film.getDirectors()));
        return newFilm;
    }

    public Collection<Film> getAll() {
        log.info("полуение списка всех фильмов");
        Collection<Film> films = filmRepository.getAll();
        for (Film f : films) {
            f.setFilmRating(filmRatingRepository.getById(f.getFilmRating().getId()));
            for (Genre g : filmRepository.getGenres(f.getId())) {
                f.getGenres().add(g);
            }
        }
        return films;
    }

    public Film getById(Integer id) {
        if (!filmRepository.exists(id)) {
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        Film film = filmRepository.getById(id);
        film.setFilmRating(filmRatingRepository.getById(film.getFilmRating().getId()));
        for (Genre g : filmRepository.getGenres(film.getId())) {
            film.getGenres().add(g);
        }
        film.setDirectors(directorRepository.getDirectorListFromFilm(film.getId()));
        return film;
    }

    public Film addLike(Integer filmId, Integer userId) {
        log.info("Пользоателю {} понравился фильм {}.", userId, filmId);
        likeRepository.add(filmId, userId);
        feedRepository.add(userId,filmId, EVENT_TYPES, OperationsEnum.ADD);
        return filmRepository.getById(filmId);
    }

    public List<Film> getDirectors(Integer directorId, String sortBy) {
        directorRepository.findById(directorId);
        return filmRepository.getDirectorFilms(directorId, sortBy);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        List<Integer> idList = getAll().stream().map(Film::getId).toList();
        if (!idList.contains(filmId))
            throw new NotFoundException(String.format(ERROR_0001.message(), filmId));
        if (!likeRepository.isFilmLikedByUser(filmId, userId)) {
            throw new NotFoundException(String.format(ERROR_0003.message(), filmId, userId));
        }
        likeRepository.remove(filmId, userId);
        feedRepository.add(userId,filmId, EVENT_TYPES, OperationsEnum.REMOVE);
        return filmRepository.getById(filmId);
    }

    public List<Film> getMostPopularByGenreAndYear(Integer count, Optional<Integer> genreId, Optional<Integer> year) {
        final Predicate<Film> filmPredicate = film -> {
            boolean genreMatched = genreId.isEmpty() || film.getGenres().stream().map(Genre::getId)
                    .anyMatch(id -> id.equals(genreId.get()));
            boolean yearMatched = year.isEmpty() || year.get().equals(film.getReleaseDate().getYear());

            return genreMatched && yearMatched;
        };

        return getAll().stream()
                .filter(filmPredicate)
                .sorted(((o1, o2) -> likeRepository.getCount(o2.getId()) - likeRepository.getCount(o1.getId())))
                .limit(count)
                .toList();
    }

    public void delete(Integer id) {
        log.debug("Удаление фильма {}.", id);
        if (!filmRepository.exists(id)) {
            log.error("Фильм с Id={} не найден!", id);
            throw new NotFoundException(String.format(ERROR_0001.message(), id));
        }
        filmRepository.delete(id);
        log.debug("Фильм {} удален.", id);
    }

    public List<FilmSearch> searchFilms(String query, String by) {
        if (!(by.contains("title") || by.contains("director") || by.contains("title,director") || by.contains("director,title") || by.contains("unknown"))) {
            log.info("Некорректное значение выборки поиска в поле BY = {}", by);
            throw new NotContentException("Некорректное значение выборки поиска");
        }
        return filmRepository.getFilmBySearch(query, by);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return getAll().stream()
                .filter(film -> likeRepository.isFilmLikedByUser(film.getId(), userId) &&
                        likeRepository.isFilmLikedByUser(film.getId(), friendId))
                .sorted(((o1, o2) -> likeRepository.getCount(o2.getId()) - likeRepository.getCount(o1.getId())))
                .toList();
    }
}
