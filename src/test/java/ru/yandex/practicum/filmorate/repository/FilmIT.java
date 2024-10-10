package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmIT {
    @Autowired
    FilmService filmService;
    @Autowired
    UserService userService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        cleanUpDatabase();
    }

    @org.junit.jupiter.api.Test
    public void filmCreated() {
        filmService.add(getTestFilm("film"));
        assertFalse(filmService.getAll().isEmpty());
    }

    @org.junit.jupiter.api.Test
    public void filmUpdated() {
        Film film = filmService.add(getTestFilm("film"));
        film.setFilmRating(new FilmRating(2, "PG"));
        filmService.update(film);
        assertEquals(filmService.getById(film.getId()).getFilmRating().getName(),
                film.getFilmRating().getName());
    }

    @org.junit.jupiter.api.Test
    void getFilmById() {
        Film film = filmService.add(getTestFilm("film"));
        assertEquals(filmService.getById(film.getId()), film);
    }

    @org.junit.jupiter.api.Test
    void notFoundThrows() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getById(-1));
    }

    @org.junit.jupiter.api.Test
    public void getPopularFilm() {
        User user = userService.add(getTestUser("email", "login"));
        Film film1 = filmService.add(getTestFilm("film1"));
        Film film2 = filmService.add(getTestFilm("film2"));
        Film film3 = filmService.add(getTestFilm("film3"));
        filmService.addLike(film2.getId(), user.getId());
        List<Film> mostPopular = filmService.getMostPopular(1);
        assertTrue(mostPopular.size() == 1);
        assertEquals(film2.getId(), mostPopular.get(0).getId());
    }

    @org.junit.jupiter.api.Test
    public void likeRemoved() {
        User user1 = userService.add(getTestUser("email1", "login1"));
        User user2 = userService.add(getTestUser("email2", "login2"));
        Film film2 = filmService.add(getTestFilm("film1"));
        Film film1 = filmService.add(getTestFilm("film1"));
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());
        assertEquals(film1.getId(), filmService.getMostPopular(1).get(0).getId());
        filmService.deleteLike(film1.getId(), user1.getId());
        filmService.deleteLike(film1.getId(), user2.getId());
        assertEquals(film2.getId(), filmService.getMostPopular(1).get(0).getId());
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
}
