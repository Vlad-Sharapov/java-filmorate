package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbStorageTest extends StorageTest {

    @Autowired
    public GenreDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        super(filmStorage, mpaStorage, genreStorage, userStorage, jdbcTemplate);
    }

    @Test
    void shouldGenreNameWhenUseMethodGetGenre() {
        Genre genre = genreStorage.getGenre(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void shouldAllGenresNameWhenUseMethodgetAllGenre() {
        List<Genre> allGenre = genreStorage.getAllGenre();
        assertEquals(6, allGenre.size());
    }
}
