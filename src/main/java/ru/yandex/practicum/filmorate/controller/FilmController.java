package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.response.SuccessResponse;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }


    @GetMapping
    public List<Film> films() {
        return filmStorage.films();
    }

    @GetMapping("/{id}")
    public Film film(@PathVariable Long id) {
        return filmStorage.findFilm(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public SuccessResponse like(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return new SuccessResponse(true);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public SuccessResponse removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        return new SuccessResponse(true);
    }

    @GetMapping("/popular")
    public List<Film> popular(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getTopFilms(count);
    }
}
