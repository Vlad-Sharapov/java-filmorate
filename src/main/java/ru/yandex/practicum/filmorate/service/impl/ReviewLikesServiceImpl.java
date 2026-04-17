package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.ReviewLikesService;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;

@RequiredArgsConstructor
@Service
public class ReviewLikesServiceImpl implements ReviewLikesService {

    private final ReviewLikesStorage reviewLikesStorage;

    public void like(int reviewId, int userId) {
        reviewLikesStorage.like(reviewId, userId);
    }

    public void dislike(int reviewId, int userId) {
        reviewLikesStorage.dislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewLikesStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewLikesStorage.deleteDislike(reviewId, userId);
    }
}
