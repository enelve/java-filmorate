package ru.yandex.practicum.filmorate.controller;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void requestCreationValidUser() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void requestCreationEmptyLogin() throws Exception {
        String user = "{\n" +
                "  \"login\": \"\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login",
                        Is.is("Логин не может содержать пробелы")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void requestCreationInvalidBirthday() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email@email.email\",\n" +
                "  \"birthday\": \"2970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday",
                        Is.is("Дата рождения не может быть в будущем")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void requestCreationInvalidEmail() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"invalid-email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        Is.is("Электронная почта не может быть пустой и должна содержать символ @")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
