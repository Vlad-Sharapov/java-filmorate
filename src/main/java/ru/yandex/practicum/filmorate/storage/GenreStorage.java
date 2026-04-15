package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreStorage {

    Genre getGenre(Integer id);

    List<Genre> getAllGenre();

    List<Genre> getFilmGenres(Long id);

    Map<Long, List<Genre>> getFilmsGenres(List<Long> ids);

    }
