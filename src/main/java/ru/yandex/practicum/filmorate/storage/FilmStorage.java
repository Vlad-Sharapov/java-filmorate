package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    List<Film> films();

    Film create(Film film);

    Film update(Film film);

    Map<Long, Film> getFilms();

    Film findFilm(Long id);
}
