package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MpaDbServiceTest extends ServiceTest {

    @Autowired
    public MpaDbServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    @Order(22)
    void shouldMpaNameWhenUseMethodGetMpa() {
        Mpa mpa = mpaService.getMpa(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @Order(23)
    void shouldAllMpaNameWhenUseMethodGetAllMpa() {
        List<Mpa> allMpa = mpaService.getAllMpa();
        assertEquals(5, allMpa.size());
    }
}
