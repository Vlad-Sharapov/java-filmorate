package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface LikeStorage {

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    Map<Long, List<Long>> getUsersLikedFilms();


}
