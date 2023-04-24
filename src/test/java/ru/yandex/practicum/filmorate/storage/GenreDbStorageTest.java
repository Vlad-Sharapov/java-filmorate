package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbStorageTest extends StorageTest {

    @Autowired
    public GenreDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage) {
        super(filmStorage, mpaStorage, genreStorage, userStorage);
    }

    @Test
    @Order(16)
    void shouldGenreNameWhenUseMethodGetGenre() {
        Genre genre = genreStorage.getGenre(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @Order(17)
    void shouldAllGenresNameWhenUseMethodgetAllGenre() {
        List<Genre> allGenre = genreStorage.getAllGenre();
        assertEquals(6, allGenre.size());
    }
}