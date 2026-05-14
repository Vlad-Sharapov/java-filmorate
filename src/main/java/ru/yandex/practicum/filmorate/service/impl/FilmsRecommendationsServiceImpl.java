package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmsRecommendationsService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmsRecommendationsServiceImpl implements FilmsRecommendationsService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final LikeStorage likeStorage;
    private final GenreDbStorage genreDbStorage;


    @Override
    public List<Film> getRecommendationFilms(Long userId, Integer from, Integer size) {
        userStorage.findUserById(userId);
        Map<Long, List<Long>> usersLikedFilms = likeStorage.getUsersLikedFilms();
        List<Long> userFilmsIds = usersLikedFilms.get(userId);
        if (userFilmsIds == null) {
            return filmStorage.topFilms(from, size);
        }
        HashSet<Long> userFilmsSet = new HashSet<>(userFilmsIds);

        Map<Long, Long> similarUsers = usersLikedFilms.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(userId))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        oterUserFilms -> oterUserFilms.getValue().stream()
                                .filter(userFilmsSet::contains)
                                .count()));

        Map<Long, Long> filmsWeights = new HashMap<>();

        similarUsers.entrySet().stream()
                .filter(similarUser -> similarUser.getValue() != 0)
                .forEach(similarUser -> {
                            Long user = similarUser.getKey();
                            Long userWeight = similarUser.getValue();
                            List<Long> userFilms = usersLikedFilms.get(user);
                    userFilms.stream()
                            .filter(filmId -> !userFilmsSet.contains(filmId))
                            .forEach(filmId -> filmsWeights.merge(filmId, userWeight, Long::sum));
                        }
                );


        List<Long> recommendationFilms = filmsWeights.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Film> filmsByIds = filmStorage.getFilmsByIds(recommendationFilms, from, size);
        Map<Long, List<Genre>> filmsGenres = genreDbStorage.getFilmsGenres(recommendationFilms);
        return filmsByIds.stream().map(film -> film.toBuilder()
                        .genres(filmsGenres.getOrDefault(film.getId(), List.of()))
                        .build())
                .collect(Collectors.toList());
    }

}
