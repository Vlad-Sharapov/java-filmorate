package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

public interface DirectorStorage {

    Long addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long director);

    List<Director> getDirectors();

    Director getDirectorById(Long id);

    List<Director> getDirectorsByFilmId(Long filmId);

    Map<Long, List<Director>> getFilmsDirectors(List<Long> ids);

    boolean directorsExist(List<Long> ids);

}
