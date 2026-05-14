package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;

@Repository
@RequiredArgsConstructor
public class ReviewLikesStorageImpl implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void like(Long reviewId, Long userId) {
        String sql =
                "INSERT INTO review_likes (review_id, is_like, user_id) " +
                        "VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, reviewId, Boolean.TRUE, userId);
        updateReviewUsefulness(reviewId, 1);
    }

    @Override
    public void dislike(Long reviewId, Long userId) {
        String sql =
                "INSERT INTO review_likes (review_id, is_like, user_id) " +
                        "VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, reviewId, Boolean.FALSE, userId);
        updateReviewUsefulness(reviewId, -1);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        String sql =
                "DELETE FROM review_likes WHERE review_id = ? AND is_like = ? AND user_id = ?";

        jdbcTemplate.update(sql, reviewId, Boolean.TRUE, userId);
        updateReviewUsefulness(reviewId, -1);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        String sql =
                "DELETE FROM review_likes WHERE review_id = ? AND is_like = ? AND user_id = ?";

        jdbcTemplate.update(sql, reviewId, Boolean.FALSE, userId);
        updateReviewUsefulness(reviewId, 1);
    }

    private void updateReviewUsefulness(Long reviewId, int value) {
        String sql = "UPDATE reviews SET useful = useful + ? WHERE id = ?";
        jdbcTemplate.update(sql, value, reviewId);
    }
}
