package ru.yandex.practicum.filmorate.controller;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class DirectorControllerTest {
    @Autowired
    DirectorController directorController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestCreationValidDirector() throws Exception {
        String director = "{\n" +
                "  \"name\": \"Petrov Controller Test\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/directors")
                        .content(director)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestCreationDirectorEmptyName() throws Exception {
        String director = "{\n" +
                "  \"name\": \"\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/directors")
                        .content(director)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        Is.is("Название не может быть пустым.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
