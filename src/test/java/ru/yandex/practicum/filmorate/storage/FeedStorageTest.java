package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedStorageTest extends StorageTest {

    @Autowired
    private FeedStorage feedStorage;

    @Autowired
    public FeedStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        super(filmStorage, mpaStorage, genreStorage, userStorage, jdbcTemplate);
    }


    @Test
    void shouldAddLikeAndRemoveFeed() {
        Long userId = userStorage.create(user);
        Long filmId = filmStorage.create(film);

        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), EventType.LIKE, Operation.ADD);
        feedStorage.addFeed(filmId, userId, Instant.now().toEpochMilli(), EventType.LIKE, Operation.REMOVE);

        List<Feed> feeds = feedStorage.getFeed(userId);
        assertEquals(2, feeds.size());
        assertEquals(userId, feeds.get(0).getUserId());
        assertEquals(filmId, feeds.get(0).getEntityId());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(EventType.LIKE, feeds.get(0).getEventType());

        assertEquals(userId, feeds.get(1).getUserId());
        assertEquals(filmId, feeds.get(1).getEntityId());
        assertEquals(Operation.REMOVE, feeds.get(1).getOperation());
        assertEquals(EventType.LIKE, feeds.get(1).getEventType());
    }

}
