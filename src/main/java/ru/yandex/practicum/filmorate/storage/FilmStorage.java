package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    List<Film> films();

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film findFilm(Long id);

    Mpa getMpa(Integer id);

    List<Mpa> getAllMpa();

    Genre getGenre(Integer id);

    List<Genre> getAllGenre();

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    Integer numOfLikes(Long filmId);

    List<Film> topFilms(Integer count);


}
