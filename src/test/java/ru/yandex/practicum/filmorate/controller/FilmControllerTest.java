package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.gsonadapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void films() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмар Бергман")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(film));
    }

    @Test
    void create() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмар Бергман")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }
    @Test
    void createWithEmptyNameResultReturnBadRequest() throws Exception {
        Film film = Film.builder().description("Один из лучших фильмов Игмара Бергмана")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createWithBirthdayFilmsDateResultReturnIsOk() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмара Бергмана")
                .releaseDate(LocalDate.of(1895,12,28)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }
    @Test
    void createWithIncorrectFilmsDateResultReturnIsBadRequest() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмара Бергмана")
                .releaseDate(LocalDate.of(1895,12,27)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createWithDescriptionFilm200SimbolsResultReturnIsBadRequest() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").
                description("78-летний профессор из Стокгольма вспоминает и пересматривает разочарования своей долгой " +
                        "жизни. Вместе с женой сына он едет на машине на вручение почетной докторской степени, " +
                        "посещая по пути места, где прошла его молодость, встречая разных людей и старых знакомых, " +
                        "вспоминая сны и былое.")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createWithNegativeDurationResultReturnIsBadRequest() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").
                description("Один из лучших фильмов Игмара Бергмана")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(-90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithZeroDurationResultReturnIsBadRequest() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").
                description("Один из лучших фильмов Игмара Бергмана")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(0L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмар Бергман")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(1);
        this.mockMvc.perform(put("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }
    @Test
    void updateFilmWithIncorrectIdResultNotFound() throws Exception {
        Film film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмар Бергман")
                .releaseDate(LocalDate.of(1957, 12, 26)).duration(90L).build();
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(1111111);
        this.mockMvc.perform(put("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isNotFound());
    }

    public static String asJsonString(final Object obj) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
            return gson.toJson(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}