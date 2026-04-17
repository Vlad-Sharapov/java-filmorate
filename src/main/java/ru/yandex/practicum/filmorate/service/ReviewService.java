package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.time.Instant;
import java.util.List;

public interface ReviewService {
    public Review addReview(Review review);

    public Review updateReview(Review review);

    public List<Review> getReviewList(int filmId, int count);

    public void deleteReview(Long id);

    public Review getReviewById(Long id);
}
