package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage genreStorage;

    @GetMapping("/genres")
    public List<Genre> genres() {
        return genreStorage.getAllGenre();
    }

    @GetMapping("/genres/{id}")
    public Genre genre(@PathVariable Integer id) {
        return genreStorage.getGenre(id);
    }
}
