package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeStorageImpl implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long userId, Long filmId) {
        String sqlQuery = "INSERT INTO enjoy (user_id, film_id) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        String sqlQuery = "DELETE enjoy WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public Map<Long, List<Long>> getUsersLikedFilms() {
        String sqlQuery = "SELECT * FROM enjoy ";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, List<Long>> result = new HashMap<>();
            while (rs.next()) {
                List<Long> userLikedFilms = result.getOrDefault(rs.getLong("user_id"), new ArrayList<>());
                userLikedFilms.add(rs.getLong("film_id"));
                result.put(rs.getLong("user_id"), userLikedFilms);
            }
            return result;
        });
    }
}
