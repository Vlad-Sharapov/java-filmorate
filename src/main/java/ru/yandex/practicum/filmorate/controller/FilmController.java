package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> films(@RequestParam(required = false, defaultValue = "0") Integer from,
                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return filmService.getAllFilms(from, size);
    }

    @GetMapping("/films/{id}")
    public Film film(@PathVariable Long id) {
        return filmService.findFilm(id);
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
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
    public List<Film> popular(@RequestParam(required = false, defaultValue = "0") Integer from,
                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        return filmService.getTopFilms(from, size);
    }

    @DeleteMapping("/films/{id}")
    public void delete(@PathVariable Long id) {
        filmService.delete(id);
    }
}
