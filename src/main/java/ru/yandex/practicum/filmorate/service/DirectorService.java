package ru.yandex.practicum.filmorate.service;


import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;


public interface DirectorService {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    List<Director> getDirectors();

    Director getDirectorById(Long id);

}
