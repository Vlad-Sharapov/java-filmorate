package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmControllerTest.asJsonString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class DirectorControllerTest extends ServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DirectorService directorService;

    @Autowired
    public DirectorControllerTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }


    @Test
    void shouldCreateDirector() throws Exception {
        this.mockMvc.perform(post("/directors")
                        .content(asJsonString(Director.builder()
                                .name("Firs").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Firs"));
    }


    @Test
    void shouldStatus400WithNameEqualsBlank() throws Exception {
        this.mockMvc.perform(post("/directors").content(asJsonString(Director.builder()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateDirector() throws Exception {
        directorService.addDirector(Director.builder().name("Firs").build());
        directorService.addDirector(Director.builder().name("Second").build());
        this.mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("*/*")
                        .content(asJsonString(Director.builder()
                                .id(1L)
                                .name("Update")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Update"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldDeleteDirector() throws Exception {
        directorService.addDirector(Director.builder().name("Firs").build());
        this.mockMvc.perform(delete(("/directors/1"))
                        .content(asJsonString(Director.builder().id(1L).build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetDirectorById() throws Exception {
        directorService.addDirector(Director.builder().name("Firs").build());
        directorService.addDirector(Director.builder().name("Second").build());

        this.mockMvc.perform(get("/directors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Firs"));
    }

    @Test
    void shouldThrow404WhereDirectorIdNotExist() throws Exception {
        directorService.addDirector(Director.builder().name("Firs").build());
        directorService.addDirector(Director.builder().name("Second").build());

        this.mockMvc.perform(get("/directors/111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllDirectors() throws Exception {
        directorService.addDirector(Director.builder().name("Firs").build());
        directorService.addDirector(Director.builder().name("Second").build());

        this.mockMvc.perform(get("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Firs"))
                .andExpect(jsonPath("$[1].name").value("Second"));
    }


}
