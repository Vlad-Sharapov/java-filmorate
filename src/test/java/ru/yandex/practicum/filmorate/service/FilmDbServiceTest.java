package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmDbServiceTest extends ServiceTest {

    private final Integer SIZE = 10;
    private final Integer FROM = 0;

    @Autowired
    public FilmDbServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void shouldFilmWithId1WhenCreateAndFindFilm() {
        filmService.create(film);
        Film film = filmService.findFilm(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void shouldFilmsListWhenUseMethodFilms() {
        filmService.create(film);
        Film film2 = filmService.create(film.toBuilder().name("test2").description("desc test").build());
        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        assertEquals(film.getName(), films.get(0).getName());
        assertEquals(film2.getId(), films.get(1).getId());
        assertEquals(film2.getName(), films.get(1).getName());
        assertEquals(film2.getDescription(), films.get(1).getDescription());
        assertEquals(film2.getReleaseDate(), films.get(1).getReleaseDate());
        assertEquals(film2.getDuration(), films.get(1).getDuration());
        assertEquals(film2.getRate(), films.get(1).getRate());
        assertEquals(film2.getGenres(), films.get(1).getGenres());
        assertEquals(film2.getMpa(), films.get(1).getMpa());
        assertEquals(List.of(), films.get(1).getDirectors());
    }

    @Test
    void shouldUpdatedUserWhenUseUpdateFilm() {
        filmService.create(film);
        Film updateTest = filmService.update(film.toBuilder().id(1L).name("Update test").build());
        assertThat(updateTest).hasFieldOrPropertyWithValue("name", "Update test");
    }

    @Test
    void shouldFilm1RateEqual1WhenUseMethodAddLike() {
        userService.create(user);
        filmService.create(film);

        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        List<User> users = userService.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmService.addLike(user1.getId(), film1.getId());
        Film likedFilm = filmService.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    void shouldFilm1RateEqual1WhenUseMethodNumOfLikes() {
        userService.create(user);
        filmService.create(film);
        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        List<User> users = userService.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmService.addLike(user1.getId(), film1.getId());
        Integer film1rate = filmService.numOfLikes(film1.getId());
        assertEquals(1, film1rate);
    }

    @Test
    void shouldTopFilmsListWhenUseMethodTopFilms() {
        userService.create(user);
        filmService.create(film);
        filmService.create(film.toBuilder().name("test2").description("desc test").build());
        userService.create(user.toBuilder().name("User2").email("b@a.ru").build());

        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        List<User> users = userService.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        User user2 = users.get(1);
        Film film2 = films.get(1);
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());
        List<Film> topFilms = filmService.getTopFilms(0, 10);
        assertEquals(film2.getId(), topFilms.get(0).getId());
        assertEquals(film1.getId(), topFilms.get(1).getId());
    }

    @Test
    void shouldFilm1RateEqual1WhenUseMethodRemoveLike() {
        userService.create(user);
        filmService.create(film);
        filmService.create(film.toBuilder().name("test2").description("desc test").build());
        userService.create(user.toBuilder().name("User2").email("b@a.ru").build());


        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        List<User> users = userService.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        User user2 = users.get(1);
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.removeLike(user1.getId(), film1.getId());
        Film likedFilm = filmService.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    void shouldRemoveFilm1WhenUseMethodDelete() {
        filmService.create(film);

        List<Film> films = filmService.getAllFilms(FROM, SIZE);
        Film film1 = films.get(0);
        filmService.delete(film1.getId());

    }

}
