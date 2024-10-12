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
class UserControllerTest {
    @Autowired
    UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestCreationValidUser() throws Exception {
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
    void requestCreationEmptyLogin() throws Exception {
        String user = "{\n" +
                "  \"login\": \"         \",\n" +
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
                        Is.is("Логин не может быть пустым.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void requestCreationInvalidBirthday() throws Exception {
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
    void requestCreationInvalidEmail() throws Exception {
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

    @Test
    void deleteExistingReturnsSuccess() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteNotExistingReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getRecommendationsNoFriendsReturnsEmptySet() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user1\",\n" +
                "  \"name\": \"name1\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email1@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/recommendations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void getRecommendationsNoLikesReturnsEmptySet() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user1\",\n" +
                "  \"name\": \"name1\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email1@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));

        String user2 = "{\n" +
                "  \"login\": \"user2\",\n" +
                "  \"name\": \"name2\",\n" +
                "  \"id\": 2,\n" +
                "  \"email\": \"email2@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user2)
                .contentType(MediaType.APPLICATION_JSON));

        String film1 = "{\n" +
                "    \"name\": \"name1\",\n" +
                "    \"description\": \"description1\",\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 100,\n" +
                "    \"mpa\": { \"id\": 1}\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film1)
                .contentType(MediaType.APPLICATION_JSON));

        // user 2 likes films 1
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"));

        // user 1 has no likes, so no recommendation is provided
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/recommendations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void getRecommendationsReturnsRecommendation() throws Exception {
        String user = "{\n" +
                "  \"login\": \"user1\",\n" +
                "  \"name\": \"name1\",\n" +
                "  \"id\": 1,\n" +
                "  \"email\": \"email1@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON));

        String user2 = "{\n" +
                "  \"login\": \"user2\",\n" +
                "  \"name\": \"name2\",\n" +
                "  \"id\": 2,\n" +
                "  \"email\": \"email2@email.email\",\n" +
                "  \"birthday\": \"1970-01-01\"\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(user2)
                .contentType(MediaType.APPLICATION_JSON));

        // users 1 and 2 are friends
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/friends/2"));

        String film1 = "{\n" +
                "    \"name\": \"name1\",\n" +
                "    \"description\": \"description1\",\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 100,\n" +
                "    \"mpa\": { \"id\": 1}\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film1)
                .contentType(MediaType.APPLICATION_JSON));

        String film2 = "{\n" +
                "    \"name\": \"name2\",\n" +
                "    \"description\": \"description2\",\n" +
                "    \"releaseDate\": \"1970-01-01\",\n" +
                "    \"duration\": 200,\n" +
                "    \"mpa\": { \"id\": 2}\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film1)
                .contentType(MediaType.APPLICATION_JSON));


        // user 1 likes film 1
        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/1"));
        // user 2 likes films 1 and 2
        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/2"));
        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/2"));

        // so film 2 is recommended for user 1
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/recommendations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(2));
    }
}
