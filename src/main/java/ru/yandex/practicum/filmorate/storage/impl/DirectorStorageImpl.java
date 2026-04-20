package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Long addDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";

        int updateRows = jdbcTemplate.update(sql,
                director.getName(), director.getId());
        if (updateRows == 0) {
            throw new UserNotFoundException("Режиссер с id - " + director.getId() + " не найден");
        }
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, directorId);
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Director getDirectorById(Long id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
         return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
        }catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Режиссер с id - %s не найден", id));
        }
    }

    @Override
    public List<Director> getDirectorsByFilmId(Long filmId) {
        String sql = "SELECT director_id id, d.name " +
                "FROM film_director fg " +
                "JOIN directors d " +
                "ON d.id = fg.director_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::makeDirector, filmId);
    }

    @Override
    public Map<Long, List<Director>> getFilmsDirectors(List<Long> ids) {
        Map<Long, List<Director>> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));

        String sql = "SELECT dir.id, dir.name, fd.film_id FROM directors dir " +
                "LEFT JOIN film_director fd " +
                "ON dir.id = fd.director_id " +
                " WHERE fd.film_id IN ("+placeholders+")";

        jdbcTemplate.query(sql,
                rs -> {
            Long filmId = rs.getLong("film_id");
            Director director = Director.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name")).build();
            result.computeIfAbsent(filmId, id -> new ArrayList<>()).add(director);
                }, ids.toArray());
        return result;
    }

    @Override
    public boolean directorsExist(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }

        List<Long> uniqueIds = ids.stream()
                .distinct()
                .collect(Collectors.toList());

        String placeholders = String.join(",", Collections.nCopies(uniqueIds.size(), "?"));

        String sql = "SELECT COUNT(*) FROM directors WHERE id IN (" + placeholders + ")";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, uniqueIds.toArray());
        return count == uniqueIds.size();
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return Director.builder()
                .id(id)
                .name(name)
                .build();

    }
}
