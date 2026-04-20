package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikesStorage {
    void like(Long reviewId, Long userId);

    void dislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
