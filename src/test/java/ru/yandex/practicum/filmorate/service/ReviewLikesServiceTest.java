package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReviewLikesServiceTest extends ServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewLikesService reviewLikesService;

    @Autowired
    public ReviewLikesServiceTest(FilmService filmService,
                                  MpaService mpaService,
                                  GenreService genreService,
                                  UserService userService,
                                  JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void shouldIncreaseUsefulWhenLikeReview() {
        Review review = createReview();
        User userForLike = userService.create(user.toBuilder()
                .email("like@user.ru")
                .login("likeUser")
                .build());

        reviewLikesService.like(review.getId(), userForLike.getId());

        Review likedReview = reviewService.getReviewById(review.getId());
        assertEquals(1, likedReview.getUseful());
    }

    @Test
    void shouldDecreaseUsefulWhenDislikeReview() {
        Review review = createReview();
        User userForDislike = userService.create(user.toBuilder()
                .email("dislike@user.ru")
                .login("dislikeUser")
                .build());

        reviewLikesService.dislike(review.getId(), userForDislike.getId());

        Review dislikedReview = reviewService.getReviewById(review.getId());
        assertEquals(-1, dislikedReview.getUseful());
    }

    @Test
    void shouldResetUsefulWhenDeleteLike() {
        Review review = createReview();
        User userForLike = userService.create(user.toBuilder()
                .email("delete-like@user.ru")
                .login("deleteLikeUser")
                .build());

        reviewLikesService.like(review.getId(), userForLike.getId());
        reviewLikesService.deleteLike(review.getId(), userForLike.getId());

        Review reviewWithoutLike = reviewService.getReviewById(review.getId());
        assertEquals(0, reviewWithoutLike.getUseful());
    }

    @Test
    void shouldResetUsefulWhenDeleteDislike() {
        Review review = createReview();
        User userForDislike = userService.create(user.toBuilder()
                .email("delete-dislike@user.ru")
                .login("deleteDislikeUser")
                .build());

        reviewLikesService.dislike(review.getId(), userForDislike.getId());
        reviewLikesService.deleteDislike(review.getId(), userForDislike.getId());

        Review reviewWithoutDislike = reviewService.getReviewById(review.getId());
        assertEquals(0, reviewWithoutDislike.getUseful());
    }

    private Review createReview() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);

        return reviewService.addReview(Review.builder()
                .content("review")
                .isPositive(true)
                .userId(createdUser.getId())
                .filmId(createdFilm.getId())
                .build());
    }

}
