package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReviewStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long addReview(Review review) {
        String sql =
                "INSERT INTO reviews " +
                        "(content, is_positive, user_id, film_id) " +
                        "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Review updateReview(Review review) {
        String sql =
                "UPDATE reviews SET " +
                        "content = ?, is_positive = ? " +
                        "WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId());

        if (updatedRows == 0) {
            throw new ReviewNotFoundException("Отзыв с id - " + review.getId() + " не найден");
        }

        return review;
    }

    @Override
    public List<Review> getReviewList(int filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

        if (filmId == -1) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::makeReview, count);
        }

        return jdbcTemplate.query(sql, this::makeReview, filmId, count);
    }

    @Override
    public void deleteReviewById(Long id) {
        String sql =
                "DELETE FROM reviews WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReviewById(Long id) {
        String sql =
                "SELECT * " +
                        "FROM reviews " +
                        "WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException(String.format("Отзыв с id - %s не найден", id));
        }
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

}
