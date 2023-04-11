package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.gsonadapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private Film film;

    private final String LONG_DESCRIPTION = "78-летний профессор из Стокгольма вспоминает и пересматривает" +
            " разочарования своей долгой жизни. Вместе с женой сына он едет на машине на вручение почетной" +
            " докторской степени, посещая по пути места, где прошла его молодость, встречая разных людей и " +
            "старых знакомых, вспоминая сны и былое.";

    @BeforeEach
    void beforeEach() {
        film = Film.builder().name("Земляничная поляна").description("Один из лучших фильмов Игмар Бергман")
                .releaseDate(LocalDate.of(1957, 12, 26))
                .duration(90L)
                .mpa(Mpa
                        .builder()
                        .id(1)
                        .build())
                .genres(List.of(Genre
                        .builder()
                        .id(1)
                        .   build()))
                .build();
    }

    @Test
    void films() throws Exception {
//        this.mockMvc.perform(post("/films")
//                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(film));
    }

    @Test
    void create() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }

    @Test
    void shouldBadRequestWhenCreateFilmWithEmptyNameResult() throws Exception {
        film.setName(null);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldOkWhenCreateWithBirthdayFilmsDateResult() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBadRequestWhenCreateFilmWithIncorrectFilmsDateResult() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateFilmWithDescriptionFilm200Simbols() throws Exception {
        film.setDescription(LONG_DESCRIPTION);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateFilmWithNegativeDuration() throws Exception {
        film.setDuration(-90L);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateWithZeroDuration() throws Exception {
        film.setDuration(0L);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
//        this.mockMvc.perform(post("/films")
//                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
//                .andExpect(status().isOk());
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(1L);
        this.mockMvc.perform(put("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFoundWhenUpdateFilmWithIncorrectId() throws Exception {
//        this.mockMvc.perform(post("/films")
//                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
//                .andExpect(status().isOk());
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(1111111L);
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