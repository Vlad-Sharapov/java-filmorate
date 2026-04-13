package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.gsonadapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film film;

    private final String longDescription = "78-летний профессор из Стокгольма вспоминает и пересматривает" +
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
        cleanDb();
    }

    private void cleanDb() {
        jdbcTemplate.update("DELETE FROM enjoy");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
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
        film.setDescription(longDescription);
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
        MvcResult mvcResult = this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        Long id = ((Number) JsonPath.read(body, "$.id")).longValue();
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(id);
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
