package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbServiceTest extends ServiceTest {

    @Autowired
    public GenreDbServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    @Order(16)
    void shouldGenreNameWhenUseMethodGetGenre() {
        Genre genre = genreService.getGenre(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @Order(17)
    void shouldAllGenresNameWhenUseMethodgetAllGenre() {
        List<Genre> allGenre = genreService.getAllGenre();
        assertEquals(6, allGenre.size());
    }
}
