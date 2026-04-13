package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenre(Integer id) {
        String sqlQuery = "SELECT * " +
                "FROM genre " +
                "WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeGenre(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Жанр с id - %s не найден", id));
        }
    }

    @Override
    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));

    }

    @Override
    public List<Genre> getFilmGenres(Long id) {
        String sql = "SELECT genre_id id, g.name " +
                "FROM film_genres fg " +
                "JOIN genre g " +
                "ON g.id = fg.genre_id " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
    }

    @Override
    public Map<Long, List<Genre>> getFilmsGenres(List<Long> ids) {
        Map<Long, List<Genre>> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genre g " +
                "ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN("+ placeholders+ ") " +
                "ORDER BY fg.film_id, g.id";

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = Genre.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();

            result.computeIfAbsent(filmId, id -> new ArrayList<>()).add(genre);
        }, ids.toArray());

        return result;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return Genre.builder().id(id).name(name).build();
    }


}
