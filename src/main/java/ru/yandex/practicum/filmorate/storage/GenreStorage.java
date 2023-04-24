package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenre(Integer id);

    List<Genre> getAllGenre();

    List<Genre> getFilmGenres(Long id);
    }
