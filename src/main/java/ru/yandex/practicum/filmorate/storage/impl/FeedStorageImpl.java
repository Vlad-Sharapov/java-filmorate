package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedStorageImpl implements FeedStorage {

    final JdbcTemplate jdbcTemplate;

    @Override
    public void addFeed(Long entityId, Long userId, Long timeStamp, EventType eventType, Operation operation) {

        String sql = "INSERT INTO event_feed (entity_id, user_id, timestamp, event_type, event_operation) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setLong(1, entityId);
            stmt.setLong(2, userId);
            stmt.setLong(3, timeStamp);
            stmt.setString(4, eventType.toString());
            stmt.setString(5, operation.toString());
            return stmt;
        }, keyHolder);
    }

    @Override
    public List<Feed> getFeed(Long id) {

        String sql = "SELECT * FROM event_feed " +
                "WHERE user_id = ? " +
                "ORDER BY id";
        return jdbcTemplate.query(sql, this::makeFeed, id);
    }

    Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .id(rs.getLong("id"))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .timestamp(rs.getLong("timestamp"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("event_operation")))
                .build();
    }
}
