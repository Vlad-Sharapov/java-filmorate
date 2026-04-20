package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public abstract class StorageTest {

    protected final FilmStorage filmStorage;
    protected final MpaStorage mpaStorage;
    protected final GenreStorage genreStorage;
    protected Film film;

    protected final UserStorage userStorage;
    protected final JdbcTemplate jdbcTemplate;
    protected User user;


    @BeforeEach
    void beforeEach() {
        film = Film.builder().name("TEST").description("DESC TEST")
                .releaseDate(LocalDate.of(1957, 12, 26))
                .duration(150L)
                .mpa(Mpa
                        .builder()
                        .id(1)
                        .build())
                .genres(List.of(Genre
                        .builder()
                        .id(1)
                        .build()))
                .build();
        user = User.builder()
                .name("Vlad")
                .email("vlds@gmail.com")
                .login("spring")
                .birthday(LocalDate.of(1997, 9, 28))
                .build();
        cleanDb();

    }

    private void cleanDb() {
        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM event_feed");
        jdbcTemplate.update("DELETE FROM enjoy");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film_director");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM directors");

        jdbcTemplate.update("ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE event_feed ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE enjoy ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE friends ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE film_genres ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1");

    }
}
