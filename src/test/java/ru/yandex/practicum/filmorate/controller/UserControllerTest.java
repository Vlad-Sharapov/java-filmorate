package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmControllerTest.asJsonString;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void users() throws Exception {
        User user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(user));
    }

    @Test
    void create() throws Exception {
        User user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
    }

    @Test
    void createWithEmptyEmailResultBadRequest() throws Exception {
        User user = User.builder().name("Vlad").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
        user.setEmail("");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithIncorrectEmailResultBadRequest() throws Exception {
        User user = User.builder().email("asdfgmail.com").name("Vlad").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithEmptyLoginResultBadRequest() throws Exception {
        User user = User.builder().name("Vlad")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
        user.setLogin("");
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void createWithLoginWithSpacesResultBadRequest() throws Exception {
        User user = User.builder().name("Vlad").login("vl sh").email("vldslv@gmail.com")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithEmptyNameResultIsOk() throws Exception {
        User user = User.builder().email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        user.setName(user.getLogin());
        this.mockMvc.perform(get("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(result -> asJsonString(user));
    }

    @Test
    void createWithIncorrectBirthdateResultBadRequest() throws Exception {
        User user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.now()).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        User user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        user.setName("Misha");
        user.setId(1);
        this.mockMvc.perform(put("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserWithIncorrectIdResultNotFound() throws Exception {
        User user = User.builder().name("Vlad").email("vldslv@gmail.com").login("spring")
                .birthday(LocalDate.of(1997,9,28)).build();
        this.mockMvc.perform(post("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isOk());
        user.setId(1111111);
        this.mockMvc.perform(put("/users")
                        .content(asJsonString(user)).contentType("application/json").accept("*/*"))
                .andExpect(status().isNotFound());
    }
}