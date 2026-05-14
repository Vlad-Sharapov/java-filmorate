package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> films(Integer from, Integer size);

    Long create(Film film);

    Film update(Film film);

    void delete(Long id);

    Film findFilm(Long id);

    List<Film> getFilmsByIds(List<Long> id, Integer from, Integer size);

    Integer numOfLikes(Long filmId);

    List<Film> topFilms(Integer from, Integer size);

    void filmGenresUpdate(Film film);

    void filmDirectorsUpdate(Film film);

    Film findFilmByName(String name);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    public List<Film> getFilmsSearchByTitle(String query);

    public List<Film> getFilmsSearchByDirectorAndTitle(String query);

    public List<Film> getFilmsSearchByDirector(String query);

}
