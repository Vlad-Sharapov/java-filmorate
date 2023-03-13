package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);
        film.addUserLike(user.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), film.getUsersLikeIt().size()));
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUser(userId);
        film.removeUserLike(user.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), film.getUsersLikeIt().size()));
    }

    public List<Film> getTopFilms(int count) {
        List<Film> films = filmStorage.films();
        return films.stream().sorted(this::compare).limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return f1.getUsersLikeIt().size() - f0.getUsersLikeIt().size();
    }

}
