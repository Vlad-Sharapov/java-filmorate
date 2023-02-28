package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmId = 0;

    @GetMapping
    public List<Film> films() {
        log.info("Количество фильмов: {}", films.values().size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Integer id = incrementFilmId();
        checkValidation(film);
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film saveFilm = films.get(film.getId());
        if (saveFilm == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HTTP Status will be NOT FOUND\n");
        }
        checkValidation(film);
        films.put(film.getId(), film);
        log.info("Фильм изменен с {} на {}", saveFilm, film);
        return film;
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ArgumentNotValidException("Слишком длинное описание");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма недействительна");
            throw new ArgumentNotValidException("Дата релиза недействительна");
        }
    }

    private int incrementFilmId() {
        return ++filmId;
    }

}
