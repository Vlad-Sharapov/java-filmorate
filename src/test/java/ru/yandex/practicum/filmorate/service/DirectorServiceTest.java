package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirectorServiceTest extends ServiceTest {

    @Autowired
    private DirectorService directorService;

    @Autowired
    public DirectorServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void shouldCreateDirector() {
        Director director = directorService.addDirector(Director.builder().name("First").build());
        assertEquals(Director.builder()
                .id(1L)
                .name("First")
                .build(), director);
    }

    @Test
    void shouldUpdateDirectorName() {
        directorService.addDirector(Director.builder().name("First").build());
        directorService.updateDirector(Director.builder().id(1L).name("Updated").build());
        assertEquals("Updated", directorService
                .getDirectorById(1L)
                .getName());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateDirectorIdEquals1111() {
        directorService.addDirector(Director.builder().name("First").build());

        assertThrows(UserNotFoundException.class, () ->
            directorService.updateDirector(Director.builder().id(111L).name("Updated").build())
        );
    }

    @Test
    void shouldGetAllDirectors() {
        directorService.addDirector(Director.builder().name("First").build());
        directorService.addDirector(Director.builder().name("Second").build());
        directorService.addDirector(Director.builder().name("Third").build());

        List<Director> directors = directorService.getDirectors();
        assertEquals(3, directors.size());
        assertEquals("First", directors.get(0).getName());
        assertEquals("Second", directors.get(1).getName());
        assertEquals("Third", directors.get(2).getName());
    }

    @Test
    void shouldGetAllDirectorsWithDirectorsCountNull() {
        List<Director> directors = directorService.getDirectors();
        assertEquals(0, directors.size());
    }

    @Test
    void shouldGetDirectorById() {
        directorService.addDirector(Director.builder().name("First").build());

        Director directorById = directorService.getDirectorById(1L);
        assertEquals("First", directorById.getName());
    }

    @Test
    void shouldThrowExceptionWhenDirectorByIdNotFound(){
        directorService.addDirector(Director.builder().name("First").build());
        directorService.addDirector(Director.builder().name("Second").build());
        directorService.addDirector(Director.builder().name("Third").build());

        assertThrows(UserNotFoundException.class, () -> directorService.getDirectorById(22L));

    }

    @Test
    void shouldGetCorrectDirectorByIdWhenSeveralDirectorsExist() {
        directorService.addDirector(Director.builder().name("First").build());
        directorService.addDirector(Director.builder().name("Second").build());
        directorService.addDirector(Director.builder().name("Third").build());

        Director directorById = directorService.getDirectorById(2L);
        assertEquals("Second", directorById.getName());
    }
    @Test
    void shouldDeleteDirector() {
        directorService.addDirector(Director.builder().name("First").build());

        directorService.deleteDirector(1L);

        assertEquals(0, directorService.getDirectors().size());
    }

}
