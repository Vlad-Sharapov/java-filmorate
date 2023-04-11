package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Component("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> films() {
        String sqlQuery = "SELECT f.\"id\", f.\"name\", " +
                "f.\"description\", " +
                "f.\"release_date\", " +
                "f.\"duration\", " +
                "f.\"mpa_id\", " +
                "COUNT(e.\"user_id\") rate " +
                "FROM \"films\" f " +
                "LEFT JOIN \"enjoy\" e ON f.\"id\" = e.\"film_id\" " +
                "GROUP BY f.\"id\"";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
        log.info("Количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQueryToFilms = "INSERT INTO \"films\" " +
                "(\"name\", " +
                "\"description\", " +
                "\"release_date\", " +
                "\"duration\", " +
                "\"mpa_id\") " +
                "VALUES (?,?,?,?,?)";
        String sqlQueryToFilmGenres = "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\") VALUES (?,?)";
        Integer mpaId = film.getMpa().getId();
        Mpa mpa = getMpa(mpaId);
        if (checkFilmExist(film)) {
            throw new ValidationException("Фильм с этим названием уже существует");
        }
        checkValidation(film);
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
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQueryToFilmGenres,
                    id,
                    genre.getId()));
        }
        log.info("Добавлен фильм: {}", film);
        film.setId(id);
        film.setMpa(mpa);
        film.setRate(numOfLikes(id));
        film.setGenres(getFilmGenres(id));
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE \"films\" " +
                "SET \"name\" = ?, " +
                "\"description\" = ?, " +
                "\"release_date\" = ?, " +
                "\"duration\" = ?, " +
                "\"mpa_id\" = ? " +
                "WHERE \"id\" = ?";
        checkValidation(film);
        Integer mpaId = film.getMpa().getId();
        Mpa mpa = getMpa(mpaId);
        if (!checkFilmExist(film.getId())) {
            log.warn(String.format("Фильм с id - %s не найден", film.getId()));
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", film.getId()));
        }
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        filmGenresUpdate(film);
        film.setMpa(mpa);
        film.setRate(numOfLikes(film.getId()));
        film.setGenres(getFilmGenres(film.getId()));
        return film;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE \"films\" " +
                "WHERE \"id\" = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film findFilm(Long id) {
        String sqlQuery = "SELECT f.\"id\", f.\"name\", " +
                "f.\"description\", " +
                "f.\"release_date\", " +
                "f.\"duration\", " +
                "f.\"mpa_id\", " +
                "COUNT(e.\"user_id\") rate " +
                "FROM \"films\" f " +
                "LEFT JOIN \"enjoy\" e ON f.\"id\" = e.\"film_id\" " +
                "WHERE f.\"id\" = ? " +
                "GROUP BY e.\"film_id\";";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
            log.info("Количество фильмов: {}", film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм с id - %s не найден", id));
        }
    }

    @Override
    public Genre getGenre(Integer id) {
        String sqlQuery = "SELECT * " +
                "FROM \"genre\" " +
                "WHERE \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeGenre(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Жанр с id - %s не найден", id));
        }
    }

    @Override
    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM \"genre\"";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));

    }

    @Override
    public void addLike(Long userId, Long filmId) {
        String sqlQuery = "INSERT INTO \"enjoy\" (\"user_id\", \"film_id\") VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        String sqlQuery = "DELETE \"enjoy\" WHERE \"user_id\" = ? AND \"film_id\" = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public Integer numOfLikes(Long filmId) {
        String sqlQuery = "SELECT COUNT(\"user_id\") num_of_likes FROM \"enjoy\" WHERE \"film_id\" = ? GROUP BY \"film_id\"";
        SqlRowSet numOfLikesRow = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (numOfLikesRow.next()) {
            return numOfLikesRow.getInt("num_of_likes");
        }
        return 0;
    }

    @Override
    public List<Film> topFilms(Integer count) {
        String sqlQuery = "SELECT f.\"id\", f.\"name\", " +
                "f.\"description\", " +
                "f.\"release_date\", " +
                "f.\"duration\", " +
                "f.\"mpa_id\", " +
                "COUNT(e.\"user_id\") rate " +
                "FROM \"films\" f " +
                "LEFT JOIN \"enjoy\" e ON f.\"id\" = e.\"film_id\" " +
                "GROUP BY e.\"film_id\" " +
                "ORDER BY COUNT(e.\"user_id\") DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public Mpa getMpa(Integer id) {
        String sqlQuery = "SELECT * " +
                "FROM \"mpa\" " +
                "WHERE \"id\" = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeMpa(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Рейтинг с id - %s не найден", id));
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM \"mpa\"";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    private void filmGenresUpdate(Film film) {
        String sqlFilmGenres = "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\") VALUES (?,?) ";

        String sqlDeleteGenres = "DELETE \"film_genres\" " +
                "WHERE \"film_id\" = ?";
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

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Long duration = rs.getLong("duration");
        int mpaId = rs.getInt("mpa_id");
        int rate = rs.getInt("rate");
        Mpa mpa = getMpa(mpaId);
        List<Genre> genres = getFilmGenres(id);
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(Mpa.builder().id(mpaId).name(mpa.getName()).build())
                .genres(genres)
                .rate(rate)
                .build();
    }

    private List<Genre> getFilmGenres(Long id) {
        String sql = "SELECT \"genre_id\" id, g.\"name\" " +
                "FROM \"film_genres\" fg " +
                "JOIN \"genre\" g " +
                "ON g.\"id\" = fg.\"genre_id\" " +
                "WHERE \"film_id\" = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return Genre.builder().id(id).name(name).build();
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        return Mpa.builder().id(id).name(name).build();
    }

    private boolean checkFilmExist(Film film) {
        String sql = "SELECT f.\"id\", " +
                "f.\"name\", " +
                "f.\"description\", " +
                "f.\"release_date\", " +
                "f.\"duration\", " +
                "f.\"mpa_id\", " +
                "COUNT(e.\"user_id\") rate " +
                "FROM \"films\" f " +
                "LEFT JOIN \"enjoy\" e " +
                "ON f.\"id\" = e.\"film_id\" " +
                "WHERE f.\"name\" = ? " +
                "AND f.\"description\" = ? " +
                "AND f.\"release_date\" = ? " +
                "AND f.\"duration\" = ? " +
                "AND f.\"mpa_id\" = ? " +
                "GROUP BY e.\"film_id\"";
        try {
            jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> makeFilm(rs),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean checkFilmExist(Long id) {
        try {
            findFilm(id);
            return true;
        } catch (FilmNotFoundException e) {
            return false;
        }
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ValidationException("Слишком длинное описание фильма");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма недействительна");
            throw new ValidationException("Дата релиза фильма недействительна");
        }
    }
}
