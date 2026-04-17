package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikesStorage {
    void like(int reviewId, int userId);

    void dislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);
}