package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest extends StorageTest {

    @Autowired
    public FilmDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage) {
        super(filmStorage, mpaStorage, genreStorage, userStorage);
    }

    @Test
    @Order(12)
    void shouldFilmWithId1WhenCreateAndFindFilm() {
        filmStorage.create(film);
        Film film = filmStorage.findFilm(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(13)
    void shouldExceptionWhenCreateCopyFilm() {
        Exception exception = assertThrows(
                Exception.class,
                () -> filmStorage.create(film));
        assertEquals("Фильм с этим названием уже существует", exception.getMessage());
    }

    @Test
    @Order(14)
    void shouldFilmsListWhenUseMethodFilms() {
        Film film2 = filmStorage.create(film.toBuilder().name("test2").description("desc test").build());
        List<Film> films = filmStorage.films();
        assertEquals(film.getName(), films.get(0).getName());
        assertEquals(film2, films.get(1));
    }

    @Test
    @Order(15)
    void shouldUpdatedUserWhenUseUpdateFilm() {
        Film updateTest = filmStorage.update(film.toBuilder().id(1L).name("Update test").build());
        assertThat(updateTest).hasFieldOrPropertyWithValue("name", "Update test");
    }

    @Test
    @Order(18)
    void shouldFilm1RateEqual1WhenUseMethodAddLike() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.addLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @Order(19)
    void shouldFilm1RateEqual1WhenUseMethodNumOfLikes() {
        List<Film> films = filmStorage.films();
        Film film1 = films.get(0);
        Integer film1rate = filmStorage.numOfLikes(film1.getId());
        assertEquals(1, film1rate);
    }

    @Test
    @Order(20)
    void shouldTopFilmsListWhenUseMethodTopFilms() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        User user2 = users.get(1);
        Film film2 = films.get(1);
        filmStorage.addLike(user2.getId(), film1.getId());
        filmStorage.addLike(user1.getId(), film2.getId());
        List<Film> topFilms = filmStorage.topFilms(10);
        assertEquals(film1.getId(), topFilms.get(0).getId());
        assertEquals(film2.getId(), topFilms.get(1).getId());
    }

    @Test
    @Order(21)
    void shouldFilm1RateEqual1WhenUseMethodRemoveLike() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.removeLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @Order(24)
    void shouldRemoveFilm1WhenUseMethodDelete() {
        List<Film> films = filmStorage.films();
        Film film1 = films.get(0);
        filmStorage.delete(film1.getId());

    }

}