package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    @Qualifier("FilmDbStorage")
    @NonNull
    private final FilmStorage filmStorage;
    @NonNull
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> films() {
        return filmStorage.films();
    }

    @GetMapping("/films/{id}")
    public Film film(@PathVariable Long id) {
        return filmStorage.findFilm(id);
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> popular(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getTopFilms(count);
    }

    @DeleteMapping("/films/{id}")
    public void delete(@PathVariable Long id) {
        filmStorage.delete(id);
    }
}
