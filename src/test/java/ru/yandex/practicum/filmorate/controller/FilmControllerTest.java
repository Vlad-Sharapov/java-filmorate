package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.gsonadapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class FilmControllerTest extends ServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DirectorService directorService;

    private final String longDescription = "78-летний профессор из Стокгольма вспоминает и пересматривает" +
            " разочарования своей долгой жизни. Вместе с женой сына он едет на машине на вручение почетной" +
            " докторской степени, посещая по пути места, где прошла его молодость, встречая разных людей и " +
            "старых знакомых, вспоминая сны и былое.";

    @Autowired
    public FilmControllerTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void films() throws Exception {
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(film.getName()));
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
        film.setName("Такси");
        film.setDescription("Фильм про таксиста и полицейского");
        film.setId(1111111L);
        this.mockMvc.perform(put("/films")
                        .content(asJsonString(film)).contentType("application/json").accept("*/*"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateFilmWithDirector() throws Exception {
        directorService.addDirector(Director.builder().name("First").build());
        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL)
                        .content(asJsonString(film.toBuilder()
                                .directors(List.of(Director.builder()
                                        .id(1L)
                                        .name("First")
                                        .build()))
                                .build())))
                .andExpect(status().isOk());
        Film film1 = filmService.findFilm(1L);
        assertEquals(1L, film1.getDirectors().get(0).getId());
        assertEquals("First", film1.getDirectors().get(0).getName());
    }

    @Test
    void shouldNotFoundWhenCreateFilmWithIncorrectDirector() throws Exception {
        directorService.addDirector(Director.builder().name("First").build());
        this.mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).accept(MediaType.ALL)
                        .content(asJsonString(film.toBuilder()
                                .directors(List.of(Director.builder()
                                        .id(111L)
                                        .name("First")
                                        .build()))
                                .build())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotFoundWhenCreateFilmWithIncorrectGenre() throws Exception {
        directorService.addDirector(Director.builder().name("First").build());
        this.mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(asJsonString(film.toBuilder()
                        .genres(List.of(Genre
                                .builder()
                                .id(111)
                                .build()))
                        .build())))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldGetFilmsByDirectorSortedByYear() throws Exception {
        Director director = directorService.addDirector(Director.builder().name("Director").build());
        Film firstFilm = filmService.create(film.toBuilder()
                .name("Old film")
                .releaseDate(LocalDate.of(1980, 1, 1))
                .directors(List.of(director))
                .build());
        Film secondFilm = filmService.create(film.toBuilder()
                .name("New film")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .directors(List.of(director))
                .build());

        this.mockMvc.perform(get("/films/director/" + director.getId())
                        .param("sortBy", "year")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(firstFilm.getId()))
                .andExpect(jsonPath("$[1].id").value(secondFilm.getId()));
    }

    @Test
    void shouldGetFilmsByDirectorSortedByLikes() throws Exception {
        Director director = directorService.addDirector(Director.builder().name("Director").build());
        Film firstFilm = filmService.create(film.toBuilder()
                .name("Less liked")
                .directors(List.of(director))
                .build());
        Film secondFilm = filmService.create(film.toBuilder()
                .name("More liked")
                .directors(List.of(director))
                .build());

        Long user1Id = userService.create(user).getId();
        Long user2Id = userService.create(user.toBuilder()
                .email("second@user.ru")
                .login("secondUser")
                .build()).getId();
        filmService.addLike(firstFilm.getId(), user1Id);
        filmService.addLike(secondFilm.getId(), user1Id);
        filmService.addLike(secondFilm.getId(), user2Id);

        this.mockMvc.perform(get("/films/director/" + director.getId())
                        .param("sortBy", "likes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(secondFilm.getId()))
                .andExpect(jsonPath("$[1].id").value(firstFilm.getId()));
    }

    @Test
    void shouldSearchFilmsByTitle() throws Exception {
        Film foundFilm = filmService.create(film.toBuilder()
                .name("Unique Search Title")
                .build());
        filmService.create(film.toBuilder()
                .name("Another Film")
                .build());

        this.mockMvc.perform(get("/films/search")
                        .param("query", "search")
                        .param("by", "title")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(foundFilm.getId()));
    }

    @Test
    void shouldSearchFilmsByDirector() throws Exception {
        Director director = directorService.addDirector(Director.builder().name("Search Director").build());
        Film foundFilm = filmService.create(film.toBuilder()
                .name("Director film")
                .directors(List.of(director))
                .build());
        filmService.create(film.toBuilder()
                .name("Other film")
                .build());

        this.mockMvc.perform(get("/films/search")
                        .param("query", "director")
                        .param("by", "director")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(foundFilm.getId()));
    }

    protected static String asJsonString(final Object obj) {
        try {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            return gson.toJson(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
