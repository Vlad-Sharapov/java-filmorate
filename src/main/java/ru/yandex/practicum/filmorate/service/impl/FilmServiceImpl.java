package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getAllFilms(Integer from, Integer size) {
        List<Film> films = filmStorage.films(from, size);
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, List<Genre>> filmsGenres=genreStorage.getFilmsGenres(filmsIds);
        return films.stream().map(film -> film.toBuilder()
                .genres(filmsGenres.get(film.getId()))
                .build())
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public Film create(Film film) throws EmptyResultDataAccessException {
        checkValidation(film);
        Integer mpaId = film.getMpa().getId();
        Mpa mpa = mpaStorage.getMpa(mpaId);
        Long id = filmStorage.create(film);
        Film addedFilm = film.toBuilder()
                .id(id)
                .mpa(mpa)
                .rate(numOfLikes(id))
                .build();
        filmStorage.filmGenresUpdate(addedFilm);
        List<Genre> filmGenres = genreStorage.getFilmGenres(id);
        addedFilm.setGenres(filmGenres);
        log.info("Добавлен фильм: {}", film);
        return addedFilm;
    }
    @Transactional
    @Override
    public Film update(Film film) {
        checkValidation(film);
        Integer mpaId = film.getMpa().getId();
        if (!checkFilmExist(film.getId())) {
            log.warn(String.format("Фильм с id - %s не найден", film.getId()));
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", film.getId()));
        }
        filmStorage.update(film);
        filmStorage.filmGenresUpdate(film);
        Mpa mpa = mpaStorage.getMpa(mpaId);
        return film.toBuilder()
                .mpa(mpa)
                .rate(numOfLikes(film.getId()))
                .genres(genreStorage.getFilmGenres(film.getId())).build();
    }

    @Override
    public void delete(Long id) {
        filmStorage.findFilm(id);
        filmStorage.delete(id);
    }

    @Override
    public Film findFilm(Long id) {
        return filmStorage.findFilm(id);
    }

    @Override
    public Integer numOfLikes(Long filmId) {
        return filmStorage.numOfLikes(filmId);
    }

    @Override
    public List<Film> topFilms(Integer count) {
        return filmStorage.topFilms(count);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        filmStorage.addLike(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        userStorage.deleteFriend(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmStorage.topFilms(count);
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ValidationException("Слишком длинное описание фильма");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма недействительна");
            throw new ValidationException("Дата релиза фильма недействительна");
        }

        if (filmStorage.findFilmByName(film.getName()) != null) {
            throw new ValidationException("Фильм с этим названием уже существует");
        }
    }

    private boolean checkFilmExist(Long id) {
        try {
            findFilm(id);
            return true;
        } catch (FilmNotFoundException e) {
            return false;
        }
    }
}
