package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReviewServiceTest extends ServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    public ReviewServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }



    @Test
    public void addReview() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);

        Review createdReview = reviewService.addReview(Review.builder()
                .content("Good film")
                .isPositive(true)
                .userId(createdUser.getId())
                .filmId(createdFilm.getId())
                .build());

        assertEquals(1L, createdReview.getId());
        assertEquals("Good film", createdReview.getContent());
        assertEquals(true, createdReview.getIsPositive());
        assertEquals(createdUser.getId(), createdReview.getUserId());
        assertEquals(createdFilm.getId(), createdReview.getFilmId());
        assertEquals(0, createdReview.getUseful());
    }




    @Test
    public void updateReview() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);
        Review createdReview = createReview(createdFilm, createdUser, "Before update", true);

        Review updatedReview = reviewService.updateReview(createdReview.toBuilder()
                .content("After update")
                .isPositive(false)
                .build());

        assertEquals(createdReview.getId(), updatedReview.getId());
        assertEquals("After update", updatedReview.getContent());
        assertEquals(false, updatedReview.getIsPositive());
        assertEquals(createdUser.getId(), updatedReview.getUserId());
        assertEquals(createdFilm.getId(), updatedReview.getFilmId());
    }

    @Test
    public void deleteReview() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);
        Review createdReview = createReview(createdFilm, createdUser, "Review to delete", true);

        reviewService.deleteReview(createdReview.getId());

        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.getReviewById(createdReview.getId()));
    }

    @Test
    public void getReviewList() {
        Film firstFilm = filmService.create(film);
        Film secondFilm = filmService.create(film.toBuilder()
                .name("Second film")
                .description("Second description")
                .build());
        User createdUser = userService.create(user);
        Review firstReview = createReview(firstFilm, createdUser, "First review", true);
        createReview(secondFilm, createdUser, "Second review", false);

        List<Review> reviews = reviewService.getReviewList(Math.toIntExact(firstFilm.getId()), 10);

        assertEquals(1, reviews.size());
        assertEquals(firstReview.getId(), reviews.get(0).getId());
        assertEquals(firstFilm.getId(), reviews.get(0).getFilmId());
    }

    @Test
    public void getAllReviewList() {
        Film firstFilm = filmService.create(film);
        Film secondFilm = filmService.create(film.toBuilder()
                .name("Second film")
                .description("Second description")
                .build());
        User createdUser = userService.create(user);
        createReview(firstFilm, createdUser, "First review", true);
        createReview(secondFilm, createdUser, "Second review", false);

        List<Review> reviews = reviewService.getReviewList(-1, 10);

        assertEquals(2, reviews.size());
    }

    @Test
    public void getReviewById() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);
        Review createdReview = createReview(createdFilm, createdUser, "Review by id", true);

        Review reviewById = reviewService.getReviewById(createdReview.getId());

        assertEquals(createdReview.getId(), reviewById.getId());
        assertEquals("Review by id", reviewById.getContent());
        assertEquals(createdUser.getId(), reviewById.getUserId());
        assertEquals(createdFilm.getId(), reviewById.getFilmId());
    }

    @Test
    public void shouldThrowWhenReviewByIdNotFound() {
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.getReviewById(999L));
    }

    @Test
    public void shouldThrowWhenAddReviewWithUnknownFilm() {
        User createdUser = userService.create(user);

        assertThrows(FilmNotFoundException.class, () -> reviewService.addReview(Review.builder()
                .content("review")
                .isPositive(true)
                .userId(createdUser.getId())
                .filmId(999L)
                .build()));
    }

    @Test
    public void shouldThrowWhenAddReviewWithUnknownUser() {
        Film createdFilm = filmService.create(film);

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(Review.builder()
                .content("review")
                .isPositive(true)
                .userId(999L)
                .filmId(createdFilm.getId())
                .build()));
    }

    @Test
    public void shouldThrowWhenUpdateReviewWithUnknownReviewId() {
        Film createdFilm = filmService.create(film);
        User createdUser = userService.create(user);

        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(Review.builder()
                .id(999L)
                .content("review")
                .isPositive(true)
                .userId(createdUser.getId())
                .filmId(createdFilm.getId())
                .build()));
    }

    @Test
    public void shouldThrowWhenDeleteUnknownReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(999L));
    }

    private Review createReview(Film film, User user, String content, Boolean isPositive) {
        return reviewService.addReview(Review.builder()
                .content(content)
                .isPositive(isPositive)
                .userId(user.getId())
                .filmId(film.getId())
                .build());
    }

}
