package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms(Integer from, Integer size);

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film findFilm(Long id);

    Integer numOfLikes(Long filmId);

    List<Film> topFilms(Integer count);

    void addLike(Long filmId, Long userId);


    void removeLike(Long filmId, Long userId);


    List<Film> getTopFilms(int count);




}
