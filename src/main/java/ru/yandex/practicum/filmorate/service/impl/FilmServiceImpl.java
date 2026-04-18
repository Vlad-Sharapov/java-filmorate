package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.EventType.LIKE;
import static ru.yandex.practicum.filmorate.utils.Operation.ADD;
import static ru.yandex.practicum.filmorate.utils.Operation.REMOVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;

    private final LikeStorage likeStorage;

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

    private final MpaStorage mpaStorage;

    private final FeedStorage feedStorage;

    @Override
    public List<Film> getAllFilms(Integer from, Integer size) {
        List<Film> films = filmStorage.films(from, size);
        return addGenresAndDirectorsToFilms(films);

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
        filmStorage.filmDirectorsUpdate(addedFilm);
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
        filmStorage.filmDirectorsUpdate(film);
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
        List<Genre> filmGenres = genreStorage.getFilmGenres(id);
        List<Director> filmDirectors = directorStorage.getDirectorsByFilmId(id);
        Film film = filmStorage.findFilm(id);
        return film.toBuilder()
                .genres(filmGenres)
                .directors(filmDirectors)
                .build();
    }

    @Override
    public Integer numOfLikes(Long filmId) {
        return filmStorage.numOfLikes(filmId);
    }

    @Override
    public List<Film> getTopFilms(Integer from, Integer size) {
        List<Film> films = filmStorage.topFilms(from, size);
        return addGenresAndDirectorsToFilms(films);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        likeStorage.addLike(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), LIKE, ADD);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findFilm(filmId);
        User user = userStorage.findUserById(userId);
        likeStorage.removeLike(user.getId(), film.getId());
        log.info(String.format("Количество лайков для фильма %s: %s", film.getName(), filmStorage.numOfLikes(filmId)));
        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), LIKE, REMOVE);
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        Director director = directorStorage.getDirectorById(directorId);
        List<Film> filmsByDirector = filmStorage.getFilmsByDirector(directorId, sortBy);
        return addGenresAndDirectorsToFilms(filmsByDirector);
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        List<String> list = new ArrayList<>(Arrays.asList(by.split(",")));

        if (list.size() == 2) {
            List<Film> films = filmStorage.getFilmsSearchByDirectorAndTitle(query);
            return addGenresAndDirectorsToFilms(films);
        }
        if (list.get(0).equalsIgnoreCase("title")) {
            List<Film> films =  filmStorage.getFilmsSearchByTitle(query);
            return addGenresAndDirectorsToFilms(films);
        }
        if (list.get(0).equalsIgnoreCase("director")) {
            List<Film> films =  filmStorage.getFilmsSearchByDirector(query);
            return addGenresAndDirectorsToFilms(films);
        } else
            throw new IllegalArgumentException("unexpected param <by> - " + by);

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

//        if (filmStorage.findFilmByName(film.getName()) != null) {
//            throw new ValidationException("Фильм с этим названием уже существует");
//        }
    }

    private boolean checkFilmExist(Long id) {
        try {
            findFilm(id);
            return true;
        } catch (FilmNotFoundException e) {
            return false;
        }
    }

    private List<Film> addGenresAndDirectorsToFilms(List<Film> films) {
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, List<Genre>> filmsGenres = genreStorage.getFilmsGenres(filmsIds);
        Map<Long, List<Director>> filmsDirectors = directorStorage.getFilmsDirectors(filmsIds);
        return films.stream().map(film -> film.toBuilder()
                        .genres(filmsGenres.getOrDefault(film.getId(), List.of()))
                        .directors(filmsDirectors.getOrDefault(film.getId(), List.of()))
                        .build())
                .collect(Collectors.toList());
    }
}
