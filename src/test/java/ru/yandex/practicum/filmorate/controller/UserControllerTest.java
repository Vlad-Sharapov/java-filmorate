package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmControllerTest.asJsonString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM event_feed");
        jdbcTemplate.update("DELETE FROM enjoy");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film_director");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM directors");

        jdbcTemplate.update("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE event_feed ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE enjoy ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE friends ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE film_genres ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1");

        user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997, 9, 28)).build();
    }

    @Test
    void users() throws Exception {
        this.mockMvc.perform(get("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create() throws Exception {
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void shouldBadRequestWhenCreateUserWithEmptyEmail() throws Exception {
        user.setEmail(null);
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
        user.setEmail("");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateUserWithIncorrectEmail() throws Exception {
        user.setEmail("asdfgmail.com@");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateUserWithEmptyLogin() throws Exception {
        user.setLogin(null);
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
        user.setLogin("");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenCreateUserWithLoginWithSpaces() throws Exception {
        user.setLogin("vl sh");
        user.setEmail("a@b.ru");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldOkWhenCreateUserWithEmptyName() throws Exception {
        user.setName("");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getLogin()));
    }

    @Test
    void shouldBadRequestWhenCreateUserWithIncorrectBirthdate() throws Exception {
        user.setBirthday(LocalDate.now());
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());

        user.setName("Misha");
        user.setId(1L);
        this.mockMvc.perform(put("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFoundWhenUpdateUserWithIncorrectId() throws Exception {
        user.setId(1111111L);
        this.mockMvc.perform(put("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isNotFound());
    }
}
