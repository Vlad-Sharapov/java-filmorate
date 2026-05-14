package ru.yandex.practicum.filmorate.service;

public interface ReviewLikesService {

    void like(Long reviewId, Long userId);

    void dislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
