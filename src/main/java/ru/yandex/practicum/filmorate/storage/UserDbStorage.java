package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> users() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?,?,?,?)";
        if (checkUserExist(user.getEmail()))
            throw new ValidationException("Пользователь с такой почтой уже существует");
        entityValidation(user);
        changeEmptyName(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(id);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id = ?";

        if (!checkUserExist(user.getId())) {
            log.warn("Пользователь с id - {} не найден", user.getEmail());
            throw new UserNotFoundException(String.format("Пользователь %s не найден", user.getEmail()));
        }
        entityValidation(user);
        changeEmptyName(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            ps.setLong(5, user.getId());
            return ps;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(id);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("Пользователь с id - %s не найден", id));
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        String sqlQuery = "SELECT * fROM users WHERE id IN(SELECT user_id2 " +
                "FROM friends " +
                "WHERE user_id1 = ?)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void addFriend(Long userId1, Long userId2) {
        String sqlQuery = "INSERT INTO friends (user_id1, user_id2) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, userId1, userId2);
    }

    @Override
    public void deleteFriend(Long userId1, Long userId2) {
        String sqlQuery = "DELETE friends WHERE user_id1 = ? AND user_id2 = ?";
        jdbcTemplate.update(sqlQuery, userId1, userId2);
    }

    @Override
    public boolean checkFriendExist(Long userId1, Long userId2) {
        String sqlQuery = "SELECT * FROM friends WHERE user_id1 = ? AND user_id2 = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFriends(rs), userId1, userId2);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public List<User> getCommonFriends(Long id1, Long id2) {
        String sqlQuery = "SELECT * FROM users WHERE id IN(SELECT f1.user_id2 " +
                "FROM friends f1 " +
                "WHERE f1.user_id1 = ?" +
                "AND f1.user_id2 = (SELECT f2.user_id2 " +
                "FROM friends f2 " +
                "WHERE f2.user_id1 = ? " +
                "))";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id1, id2);
    }

    @Override
    public boolean setStatus(Long userId1, Long userId2, boolean status) {
        String sqlQuery = "UPDATE friends SET status = ? WHERE user_id1 = ? AND user_id2 = ?";
        jdbcTemplate.update(sqlQuery, status, userId1, userId2);
        return true;
    }

    private void entityValidation(User saveUser) {
        if (saveUser.getLogin().contains(" ")) {
            log.warn("Некорректные данные (Аргумент параметра \"login\" имеет пробелы).");
            throw new ValidationException();
        }
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean checkUserExist(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), email);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean checkUserExist(Long id) {
        try {
            findUserById(id);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    private Friends makeFriends(ResultSet rs) throws SQLException {
        int userId1 = rs.getInt("user_id1");
        int userId2 = rs.getInt("user_id2");
        boolean status = rs.getBoolean("status");

        return Friends.builder().user(userId1).friend(userId2).status(status).build();
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return User.builder().id(id).email(email).login(login).name(name).birthday(birthday).build();
    }
}



