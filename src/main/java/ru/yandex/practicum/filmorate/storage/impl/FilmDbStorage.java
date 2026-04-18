package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> films(Integer from, Integer size) {
        int offset = from * size;
        String sqlQuery = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "m.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa m ON f.MPA_ID = m.ID " +
                "GROUP BY f.id " +
                "LIMIT ? OFFSET ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, size, offset);
        log.info("Количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Long create(Film film) {
        String sqlQueryToFilms = "INSERT INTO films " +
                "(name, " +
                "description, " +
                "release_date, " +
                "duration, " +
                "mpa_id) " +
                "VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQueryToFilms, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films " +
                "SET name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE films " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film findFilm(Long id) {
        String sqlQuery = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.MPA_ID, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "WHERE f.id = ? " +
                "GROUP BY f.id;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
            log.info("Количество фильмов: {}", film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", id));
        }
    }

    @Override
    public List<Film> getFilmsByIds(List<Long> ids, Integer from, Integer size) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Integer offset = from * size;

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.MPA_ID, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "WHERE f.id in (" + placeholders + ") " +
                "GROUP BY f.id " +
                "LIMIT ? OFFSET ?";

        List<Object> params = new ArrayList<>(ids);
        params.add(size);
        params.add(offset);

        return jdbcTemplate.query(sql, this::makeFilm, params.toArray());
    }

    @Override
    public Film findFilmByName(String name) {
        String sqlQuery = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.MPA_ID, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "WHERE f.NAME = ? " +
                "GROUP BY f.id, e.film_id;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, name);
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if (directorId == null || directorId == 0) {
            throw new FilmNotFoundException("Режиссер не найден.");
        }
        String sql = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.MPA_ID, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "LEFT JOIN film_director d ON d.film_id = f.id " +
                "WHERE d.director_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY " + getOrderBy(sortBy) + ";";

        return jdbcTemplate.query(sql, this::makeFilm, directorId);
    }

    @Override
    public Integer numOfLikes(Long filmId) {
        String sqlQuery = "SELECT COUNT(user_id) num_of_likes FROM enjoy WHERE film_id = ? GROUP BY film_id";
        SqlRowSet numOfLikesRow = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (numOfLikesRow.next()) {
            return numOfLikesRow.getInt("num_of_likes");
        }
        return 0;
    }

    @Override
    public List<Film> topFilms(Integer from, Integer size) {
        String sqlQuery = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "GROUP BY f.id " +
                "ORDER BY rate DESC " +
                "LIMIT ? " +
                "OFFSET ? ";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, size, from);
    }

    @Override
    public void filmGenresUpdate(Film film) {
        String sqlFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?,?) ";

        String sqlDeleteGenres = "DELETE film_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        film.getGenres()
                .stream()
                .distinct()
                .forEach(genre -> jdbcTemplate.update(sqlFilmGenres,
                        film.getId(),
                        genre.getId()));
    }

    @Override
    public void filmDirectorsUpdate(Film film) {

        String sqlFilmDirectors = "INSERT INTO film_director (film_id, director_id) VALUES (?,?) ";

        String sqlDeleteDirectors = "DELETE film_director WHERE film_id = ?";

        jdbcTemplate.update(sqlDeleteDirectors, film.getId());

        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }
        film.getDirectors().stream()
                .distinct()
                .forEach(director -> jdbcTemplate.update(sqlFilmDirectors,
                        film.getId(),
                        director.getId()));

    }

    @Override
    public List<Film> getFilmsSearchByTitle(String query) {
        String sql = "SELECT f.id, f.name, " +
        "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e on f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "WHERE f.name ILIKE '%'||?||'%' " +
                "GROUP BY f.id " +
                "ORDER BY rate DESC";
        return jdbcTemplate.query(sql, this::makeFilm, query);
    }

    @Override
    public List<Film> getFilmsSearchByDirectorAndTitle(String query) {
        String sql = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e on f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "WHERE f.name ILIKE '%'||?||'%' OR " +
                "f.id IN " +
                "(SELECT fd.film_id FROM film_director fd " +
                "LEFT JOIN directors d ON fd.director_id = d.id " +
                "WHERE d.name ILIKE '%'||?||'%') " +
                "GROUP BY f.id " +
                "ORDER BY rate DESC";

        return jdbcTemplate.query(sql, this::makeFilm, query, query);
    }

    @Override
    public List<Film> getFilmsSearchByDirector(String query) {

        String sql = "SELECT f.id, f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.MPA_ID, " +
                "M.NAME mpa_name, " +
                "COUNT(e.user_id) rate " +
                "FROM films f " +
                "LEFT JOIN enjoy e ON f.id = e.film_id " +
                "LEFT JOIN mpa M on f.MPA_ID = M.id " +
                "LEFT JOIN film_director fd ON fd.film_id = f.id " +
                "JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE d.name ILIKE '%'||?||'%' " +
                "GROUP BY f.id " +
                "ORDER BY rate DESC";

        return jdbcTemplate.query(sql, this::makeFilm, query);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Long duration = rs.getLong("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");
        int rate = rs.getInt("rate");
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(Mpa.builder().id(mpaId).name(mpaName).build())
                .rate(rate)
                .build();
    }


    private String getOrderBy(String sortBy) {
        return switch (sortBy) {
            case "likes" -> "COUNT(e.user_id) ";
            case "year" -> "f.release_date ";
            default -> "f.id ASC";
        };
    }
}
