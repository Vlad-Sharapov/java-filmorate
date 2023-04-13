package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
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

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return Genre.builder().id(id).name(name).build();
    }

}
