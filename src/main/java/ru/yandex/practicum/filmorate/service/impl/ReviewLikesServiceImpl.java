package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.ReviewLikesService;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;

@RequiredArgsConstructor
@Service
public class ReviewLikesServiceImpl implements ReviewLikesService {

    private final ReviewLikesStorage reviewLikesStorage;

    public void like(Long reviewId, Long userId) {
        reviewLikesStorage.like(reviewId, userId);
    }

    public void dislike(Long reviewId, Long userId) {
        reviewLikesStorage.dislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewLikesStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewLikesStorage.deleteDislike(reviewId, userId);
    }
}
