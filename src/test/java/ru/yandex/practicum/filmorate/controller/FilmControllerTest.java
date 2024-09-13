package ru.yandex.practicum.filmorate.controller;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    FilmController filmController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestCreationValidFilm() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"name\",\n" +
                "    \"description\": \"description\",\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 200\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestCreationEmptyName() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"\",\n" +
                "    \"description\": \"description\",\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        Is.is("Название не может быть пустым.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestCreationInvalidReleaseDate() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"name\",\n" +
                "    \"description\": \"description\",\n" +
                "    \"releaseDate\": \"0001-01-01\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate",
                        Is.is("дата релиза — не раньше 28 декабря 1895 года.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestCreationInvalidDescription() throws Exception {
        String longDescription = "a".repeat(201);
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"name\",\n" +
                "    \"description\": \"" + longDescription + "\" ,\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description",
                        Is.is("Максимальная длина описания — 200 символов.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
