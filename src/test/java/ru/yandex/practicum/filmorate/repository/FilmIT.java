package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmIT {
    @Autowired
    FilmService filmService;
    @Autowired
    UserService userService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    GenreRepository genreRepository;

    @AfterEach
    void afterEach() {
        cleanUpDatabase();
    }

    @Test
    void filmCreated() {
        filmService.add(getTestFilm("film"));
        assertFalse(filmService.getAll().isEmpty());
    }

    @Test
    void filmUpdated() {
        Film film = filmService.add(getTestFilm("film"));
        film.setFilmRating(new FilmRating(2, "PG"));
        filmService.update(film);
        assertEquals(filmService.getById(film.getId()).getFilmRating().getName(), film.getFilmRating().getName());
    }

    @Test
    void getFilmById() {
        Film film = filmService.add(getTestFilm("film"));
        assertEquals(filmService.getById(film.getId()), film);
    }

    @Test
    void notFoundThrows() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getById(-1));
    }

    @Test
    void getPopularFilm() {
        User user = userService.add(getTestUser("email", "login"));
        Film film1 = filmService.add(getTestFilm("film1"));
        Film film2 = filmService.add(getTestFilm("film2"));
        Film film3 = filmService.add(getTestFilm("film3"));
        filmService.addLike(film2.getId(), user.getId());
        List<Film> mostPopular = filmService.getMostPopularByGenreAndYear(1, Optional.empty(), Optional.empty());
        assertEquals(1, mostPopular.size());
        assertEquals(film2.getId(), mostPopular.get(0).getId());
    }

    @Test
    @Transactional
    void getPopularFilmByGenreAndReleaseYear() {
        User user = userService.add(getTestUser("email", "login"));
        Genre genre = genreRepository.getAll().stream().findAny().orElseThrow();
        filmService.add(getTestFilm("film1", LocalDate.now().minusYears(11), Set.of(genre)));
        filmService.add(getTestFilm("film2", LocalDate.now().minusYears(10), Set.of(genre)));
        filmService.add(getTestFilm("film3", LocalDate.now().minusYears(9), Set.of(genre)));
        filmService.add(getTestFilm("film4", LocalDate.now().minusYears(10), Set.of()));
        filmService.getAll().forEach(film -> filmService.addLike(film.getId(), user.getId()));

        List<Film> mostPopular = filmService.getMostPopularByGenreAndYear(Integer.MAX_VALUE,
                Optional.of(genre.getId()),
                Optional.of(2014));

        assertEquals(1, mostPopular.size());
        assertEquals("film2", mostPopular.get(0).getName());
    }

    @Test
    void likeRemoved() {
        User user1 = userService.add(getTestUser("email1", "login1"));
        User user2 = userService.add(getTestUser("email2", "login2"));
        Film film2 = filmService.add(getTestFilm("film1"));
        Film film1 = filmService.add(getTestFilm("film1"));
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());
        assertEquals(film1.getId(), filmService.getMostPopularByGenreAndYear(1, Optional.empty(), Optional.empty()).get(0).getId());
        filmService.deleteLike(film1.getId(), user1.getId());
        filmService.deleteLike(film1.getId(), user2.getId());
        assertEquals(film2.getId(), filmService.getMostPopularByGenreAndYear(1, Optional.empty(), Optional.empty()).get(0).getId());
    }

    private void cleanUpDatabase() {
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM likes");
    }

    private User getTestUser(String email, String login) {
        return new User(null, email, login, "name", LocalDate.now().minusYears(20));
    }

    private Film getTestFilm(String name) {
        return new Film(null, name, "description", LocalDate.now().minusYears(20), 1, new FilmRating(1, "G"), new HashSet<>(), new ArrayList<>());
    }

    private Film getTestFilm(String name, LocalDate releaseDate, Set<Genre> genres) {
        return new Film(null, name, "description", releaseDate, 1, new FilmRating(1, "G"), genres, List.of());
    }
}
