package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {


    Genre getGenre(Integer id);

    List<Genre> getAllGenre();

    List<Genre> getFilmGenres(Long id);

}
