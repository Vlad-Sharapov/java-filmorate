package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Long addReview(Review review);

    Review updateReview(Review review);

    List<Review> getReviewList(int filmId, int count);

    void deleteReviewById(Long id);

    Review getReviewById(Long id);
}
