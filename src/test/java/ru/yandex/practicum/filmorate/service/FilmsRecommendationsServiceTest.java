package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
public class FilmsRecommendationsServiceTest extends ServiceTest {

    @Autowired
    private FilmsRecommendationsService filmsRecommendationsService;

    @Autowired
    public FilmsRecommendationsServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        userService.create(user);
        userService.create(user.toBuilder()
                .name("user2")
                .email("a@b.ru")
                .build());
        userService.create(user.toBuilder()
                .name("user3")
                .email("b@b.ru")
                .build());

        filmService.create(film);
        filmService.create(film.toBuilder()
                .name("film2").build());
        filmService.create(film.toBuilder()
                .name("film3").build());
    }


    @Test
    public void shouldGetFilmsRecommendationsById() {

        List<User> users = userService.users();
        List<Film> allFilms = filmService.getAllFilms(0, 10);

        filmService.addLike(allFilms.get(0).getId(), users.get(0).getId());
        filmService.addLike(allFilms.get(0).getId(), users.get(1).getId());
        filmService.addLike(allFilms.get(1).getId(), users.get(1).getId());
        filmService.addLike(allFilms.get(0).getId(), users.get(2).getId());
        filmService.addLike(allFilms.get(1).getId(), users.get(2).getId());
        filmService.addLike(allFilms.get(2).getId(), users.get(2).getId());

        List<Film> recommendationFilmsUser1 = filmsRecommendationsService.getRecommendationFilms(users.get(1).getId(), 0, 10);
        assertEquals(1L, recommendationFilmsUser1.size());
        assertEquals(allFilms.get(2).getId(), recommendationFilmsUser1.get(0).getId());
        assertEquals(1, recommendationFilmsUser1.get(0).getGenres().size());

        List<Film> recommendationFilmsUser0 = filmsRecommendationsService.getRecommendationFilms(users.get(0).getId(), 0, 10);
        assertEquals(2L, recommendationFilmsUser0.size());
        assertEquals(allFilms.get(1).getId(), recommendationFilmsUser0.get(0).getId());
        assertEquals(allFilms.get(2).getId(), recommendationFilmsUser0.get(1).getId());

        List<Film> recommendationFilmsUser2 = filmsRecommendationsService.getRecommendationFilms(users.get(2).getId(), 0, 10);
        assertEquals(0L, recommendationFilmsUser2.size());
    }

    @Test
    public void shouldGetFilmsRecommendationsWhenUserHasNoLikeFilms() {

        List<User> users = userService.users();
        List<Film> allFilms = filmService.getAllFilms(0, 10);

        filmService.addLike(allFilms.get(0).getId(), users.get(1).getId());
        filmService.addLike(allFilms.get(1).getId(), users.get(1).getId());
        filmService.addLike(allFilms.get(1).getId(), users.get(2).getId());
        filmService.addLike(allFilms.get(2).getId(), users.get(2).getId());

        List<Film> recommendationFilmsUser1 = filmsRecommendationsService.getRecommendationFilms(users.get(0).getId(), 0, 10);
        assertEquals(3L, recommendationFilmsUser1.size());
        assertEquals(allFilms.get(1).getId(), recommendationFilmsUser1.get(0).getId());

    }

}
