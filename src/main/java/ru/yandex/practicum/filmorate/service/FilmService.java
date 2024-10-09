package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.*;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.exception.Error.*;

@Service
@Slf4j
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmRatingRepository filmRatingRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;

    @Autowired
    public FilmService(@Qualifier("FilmDatabaseRepository") FilmRepository filmRepository, FilmRatingRepository filmRatingRepository,
                       LikeRepository likeRepository, GenreRepository genreRepository, DirectorRepository directorRepository) {
        this.filmRepository = filmRepository;
        this.filmRatingRepository = filmRatingRepository;
        this.likeRepository = likeRepository;
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
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
        return filmRepository.getById(filmId);
    }

    public List<Film> getMostPopular(Integer count) {
        return filmRepository.getAll().stream()
                .sorted(((o1, o2) -> likeRepository.getCount(o2.getId()) - likeRepository.getCount(o1.getId())))
                .limit(count)
                .toList();
    }
}
