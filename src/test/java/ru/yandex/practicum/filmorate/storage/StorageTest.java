package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageTest {

    protected final FilmStorage filmStorage;
    protected final MpaStorage mpaStorage;
    protected final GenreStorage genreStorage;
    protected Film film;

    protected final UserStorage userStorage;
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

    }
}
