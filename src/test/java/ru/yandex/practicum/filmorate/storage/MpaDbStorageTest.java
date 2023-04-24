package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MpaDbStorageTest extends StorageTest {

    @Autowired
    public MpaDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage) {
        super(filmStorage, mpaStorage, genreStorage, userStorage);
    }

    @Test
    @Order(22)
    void shouldMpaNameWhenUseMethodGetMpa() {
        Mpa mpa = mpaStorage.getMpa(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @Order(23)
    void shouldAllMpaNameWhenUseMethodGetAllMpa() {
        List<Mpa> allMpa = mpaStorage.getAllMpa();
        assertEquals(5, allMpa.size());
    }
}