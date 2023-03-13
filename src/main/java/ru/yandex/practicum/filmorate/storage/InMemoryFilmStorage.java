package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GeneratorId;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final GeneratorId generatorFilmId;

    private final Map<Long, Film> films = new HashMap<>();

    @Autowired
    public InMemoryFilmStorage(GeneratorId generatorFilmId) {
        this.generatorFilmId = generatorFilmId;
    }

    public List<Film> films() {
        log.info("Количество фильмов: {}", films.values().size());
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        checkValidation(film);
        Long id = generatorFilmId.incrementId();
        film.setId(id);
        film.setUsersLikeIt(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    public Film update(Film film) {
        Film saveFilm = films.get(film.getId());
        if (saveFilm == null) {
            log.warn(String.format("Фильм с id - %s не найден", film.getId()));
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", film.getId()));
        }
        checkValidation(film);
        if (film.getUsersLikeIt() == null) {
            film.setUsersLikeIt(saveFilm.getUsersLikeIt());
        }
        films.put(film.getId(), film);
        log.info("Фильм изменен с {} на {}", saveFilm, film);
        return film;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film findFilm(Long id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn(String.format("Фильм с id - %s не найден", id));
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", id));
        }
        return film;
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ArgumentNotValidException("Слишком длинное описание фильма");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма недействительна");
            throw new ArgumentNotValidException("Дата релиза фильма недействительна");
        }
    }

}
