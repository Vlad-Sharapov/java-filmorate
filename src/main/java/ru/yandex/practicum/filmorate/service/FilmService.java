package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        filmStorage.addLike(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        userStorage.deleteFriend(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.topFilms(count);
    }
}
