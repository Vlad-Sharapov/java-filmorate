package ru.yandex.practicum.filmorate.service;

public interface ReviewLikesService {

    public void like(int reviewId, int userId);

    public void dislike(int reviewId, int userId);

    public void deleteLike(int reviewId, int userId);

    public void deleteDislike(int reviewId, int userId);
}
