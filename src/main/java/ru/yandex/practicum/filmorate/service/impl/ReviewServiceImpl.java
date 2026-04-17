package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.utils.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.utils.Operation.*;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final FeedStorage feedStorage;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review addReview(Review review) {
        filmStorage.findFilm(review.getFilmId());
        userStorage.findUserById(review.getUserId());
        Long reviewId = reviewStorage.addReview(review);

        feedStorage.addFeed(reviewId, review.getUserId(), Instant.now().toEpochMilli(), REVIEW, ADD);
        return review.toBuilder()
                .id(reviewId)
                .build();
    }

    public Review updateReview(Review review) {
        filmStorage.findFilm(review.getFilmId());
        userStorage.findUserById(review.getUserId());
        Review review1 = reviewStorage.updateReview(review);
        feedStorage.addFeed(review1.getId(), review1.getUserId(), Instant.now().toEpochMilli(), REVIEW, UPDATE);
        return review1;
    }

    public List<Review> getReviewList(int filmId, int count) {
        return reviewStorage.getReviewList(filmId, count);
    }

    public void deleteReview(Long id) {
        feedStorage.addFeed(id, reviewStorage.getReviewById(id).getUserId(), Instant.now().toEpochMilli(), REVIEW, REMOVE);
        reviewStorage.deleteReviewById(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }
}
