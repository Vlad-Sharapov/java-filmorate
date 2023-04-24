package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmControllerTest.asJsonString;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997, 9, 28)).build();
    }

    @Test
    void users() throws Exception {
        this.mockMvc.perform(get("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(user));
    }

    @Test
    void create() throws Exception {
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
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
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldOkWhenCreateUserWithEmptyName() throws Exception {

        user.setName(user.getLogin());
        this.mockMvc.perform(get("/users")
                .accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(user));
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