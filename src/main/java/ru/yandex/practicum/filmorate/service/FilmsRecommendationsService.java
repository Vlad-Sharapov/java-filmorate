package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmsRecommendationsService {

    List<Film> getRecommendationFilms(Long userId, Integer from, Integer size);

}
