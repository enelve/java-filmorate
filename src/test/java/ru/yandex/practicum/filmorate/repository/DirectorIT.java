package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
public class DirectorIT {
    @Autowired
    DirectorService directorService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        cleanUpDatabase();
    }

    @org.junit.jupiter.api.Test
    public void directorCreated() {
        directorService.save(getTestDirector("Petrov"));
        assertFalse(directorService.getAll().isEmpty());
    }

    @org.junit.jupiter.api.Test
    public void directorUpdate() {
        Director director = directorService.save(getTestDirector("Petrov 2"));
        directorService.update(director);
        assertEquals(directorService.findById(director.getId()).getName(), director.getName());
    }

    @org.junit.jupiter.api.Test
    void getDirectorById() {
        Director director = directorService.save(getTestDirector("Petrov 3"));
        assertEquals(directorService.findById(director.getId()), director);
    }

    private void cleanUpDatabase() {
        jdbcTemplate.execute("DELETE FROM director");
    }

    private Director getTestDirector(String name) {
        return new Director(null, name);
    }
}
