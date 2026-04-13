package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureTestDatabase
class FilmDbStorageTest extends StorageTest {

    private final Integer SIZE = 10;
    private final Integer FROM = 0;

    @Autowired
    public FilmDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        super(filmStorage, mpaStorage, genreStorage, userStorage, jdbcTemplate);
    }

    @Test
    void shouldFilmWithId1WhenCreateAndFindFilm() {
        filmStorage.create(film);
        Film film = filmStorage.findFilm(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void shouldFilmsListWhenUseMethodFilms() {
        filmStorage.create(film);
        Long film2Id = filmStorage.create(film.toBuilder().name("test2").description("desc test").build());
        List<Film> films = filmStorage.films(FROM, SIZE);
        assertEquals(film.getName(), films.get(0).getName());
        assertEquals(film2Id, films.get(1).getId());
    }

    @Test
    void shouldUpdatedUserWhenUseUpdateFilm() {
        Film updateTest = filmStorage.update(film.toBuilder().id(1L).name("Update test").build());
        assertThat(updateTest).hasFieldOrPropertyWithValue("name", "Update test");
    }

    @Test
    void shouldFilm1RateEqual1WhenUseMethodAddLike() {
        userStorage.create(user);
        filmStorage.create(film);
        List<Film> films = filmStorage.films(FROM, SIZE);
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.addLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    void shouldFilm1RateEqual1WhenUseMethodNumOfLikes() {
        userStorage.create(user);
        filmStorage.create(film);
        List<Film> films = filmStorage.films(FROM, SIZE);
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.addLike(user1.getId(), film1.getId());
        Integer film1rate = filmStorage.numOfLikes(film1.getId());
        assertEquals(1, film1rate);
    }

    @Test
    void shouldTopFilmsListWhenUseMethodTopFilms() {
        filmStorage.create(film);
        userStorage.create(user);
        userStorage.create(user.toBuilder().name("User2").build());
        filmStorage.create(film.toBuilder().name("test2").description("desc test").build());

        List<Film> films = filmStorage.films(FROM, SIZE);
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
    void shouldFilm1RateEqual1WhenUseMethodRemoveLike() {
        filmStorage.create(film);
        userStorage.create(user);
        userStorage.create(user.toBuilder().name("User2").build());

        List<Film> films = filmStorage.films(FROM, SIZE);
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        User user2 = users.get(1);
        filmStorage.addLike(user2.getId(), film1.getId());
        filmStorage.addLike(user1.getId(), film1.getId());
        filmStorage.removeLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    void shouldRemoveFilm1WhenUseMethodDelete() {
        filmStorage.create(film);

        List<Film> films = filmStorage.films(FROM, SIZE);
        Film film1 = films.get(0);
        filmStorage.delete(film1.getId());

    }

}
