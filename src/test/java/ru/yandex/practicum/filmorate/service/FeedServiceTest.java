package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedServiceTest extends ServiceTest {

    @Autowired
    private FeedService feedService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    public FeedServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }


    @Test
    void getLikeFeed() {

        Film film1 = filmService.create(film);
        userService.create(user);
        filmService.create(film);
        User user1 = userService.create(user.toBuilder().email("a@b.ru").build());

        filmService.addLike(film1.getId(), user1.getId());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(1, feeds.size());
        assertEquals(EventType.LIKE, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(film1.getId(), feeds.get(0).getEntityId());
    }

    @Test
    void getFriendFeed() {

        User user1 = userService.create(user.toBuilder().email("a@b.ru").build());
        User user2 = userService.create(user);

        userService.addFriend(user1.getId(), user2.getId());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(1, feeds.size());
        assertEquals(EventType.FRIEND, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(user2.getId(), feeds.get(0).getEntityId());
    }

    @Test
    void shouldAddLikeRemoveFeedWhenRemoveLike() {
        Film film1 = filmService.create(film);
        userService.create(user);
        filmService.create(film);
        User user1 = userService.create(user.toBuilder().email("a@b.ru").build());

        filmService.addLike(film1.getId(), user1.getId());
        filmService.removeLike(film1.getId(), user1.getId());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(2, feeds.size());
        assertEquals(EventType.LIKE, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(film1.getId(), feeds.get(0).getEntityId());
        assertEquals(EventType.LIKE, feeds.get(1).getEventType());
        assertEquals(Operation.REMOVE, feeds.get(1).getOperation());
        assertEquals(user1.getId(), feeds.get(1).getUserId());
        assertEquals(film1.getId(), feeds.get(1).getEntityId());
    }

    @Test
    void shouldAddFeedWhenRemoveFriend() {

        User user1 = userService.create(user.toBuilder().email("a@b.ru").build());
        User user2 = userService.create(user);

        userService.addFriend(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(2, feeds.size());
        assertEquals(EventType.FRIEND, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(user2.getId(), feeds.get(0).getEntityId());
        assertEquals(EventType.FRIEND, feeds.get(1).getEventType());
        assertEquals(Operation.REMOVE, feeds.get(1).getOperation());
        assertEquals(user1.getId(), feeds.get(1).getUserId());
        assertEquals(user2.getId(), feeds.get(1).getEntityId());
    }

    @Test
    void getReviewFeed() {

        Film film1 = filmService.create(film);
        User user1 = userService.create(user);
        Review review = reviewService.addReview(Review.builder()
                .content("asdf")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build());
        filmService.create(film);
        userService.create(user.toBuilder().email("a@b.ru").build());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(1, feeds.size());
        assertEquals(EventType.REVIEW, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(review.getId(), feeds.get(0).getEntityId());
    }

    @Test
    void shouldAddReviewRemoveFeedWhenReviewFeedRemoved() {

        Film film1 = filmService.create(film);
        User user1 = userService.create(user);
        Review review = reviewService.addReview(Review.builder()
                .content("asdf")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId()).build());
        reviewService.deleteReview(review.getId());

        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(2, feeds.size());
        assertEquals(EventType.REVIEW, feeds.get(0).getEventType());
        assertEquals(Operation.ADD, feeds.get(0).getOperation());
        assertEquals(user1.getId(), feeds.get(0).getUserId());
        assertEquals(review.getId(), feeds.get(0).getEntityId());
        assertEquals(EventType.REVIEW, feeds.get(1).getEventType());
        assertEquals(Operation.REMOVE, feeds.get(1).getOperation());
        assertEquals(user1.getId(), feeds.get(1).getUserId());
        assertEquals(review.getId(), feeds.get(1).getEntityId());
    }



    @Test
    void shouldGetFeedWhenFeedCountBlank() {

        filmService.create(film);
        User user1 = userService.create(user);
        filmService.create(film);
        userService.create(user.toBuilder().email("a@b.ru").build());


        List<Feed> feeds = feedService.getFeed(user1.getId());

        assertEquals(0, feeds.size());
    }
}
