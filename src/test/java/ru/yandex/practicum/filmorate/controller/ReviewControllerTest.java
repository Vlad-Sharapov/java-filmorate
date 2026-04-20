package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmControllerTest.asJsonString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewControllerTest extends ServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public ReviewControllerTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }


    @Test
    public void addReview() throws Exception {
        Film film1 = filmService.create(film);
        User user1 = userService.create(user);

        this.mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(asJsonString(Review.builder()
                                .filmId(film1.getId())
                                .userId(user1.getId())
                                .content("qwerty")
                                .isPositive(true)
                                .build()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").exists())
                .andExpect(jsonPath("$.content").value("qwerty"));
    }


    @Test
    public void updateReview() throws Exception {
        Review review = createReview();

        this.mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(asJsonString(review.toBuilder().content("trewq").isPositive(false).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(review.getId()))
                .andExpect(jsonPath("$.content").value("trewq"));

    }

    @Test
    public void deleteReview() throws Exception {
        Review review = createReview();

        this.mockMvc.perform(delete("/reviews/" + review.getId())
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());

    }

    @Test
    public void getReviewList() throws Exception {
        Review review = createReview();

        this.mockMvc.perform(get("/reviews")
                        .param("filmId", String.valueOf(review.getFilmId()))
                        .param("count", "10")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    public void getReviewById() throws Exception {
        Review review = createReview();

        this.mockMvc.perform(get("/reviews/" + review.getId())
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(review.getId()));

    }

    @Test
    public void shouldNotFoundWhenGetReviewByIdNotFound() throws Exception {
        this.mockMvc.perform(get("/reviews/999")
                        .accept(MediaType.ALL))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldBadRequestWhenAddReviewWithBlankContent() throws Exception {
        Film film1 = filmService.create(film);
        User user1 = userService.create(user);

        expectBadRequestOnCreate(Review.builder()
                .filmId(film1.getId())
                .userId(user1.getId())
                .content("")
                .isPositive(true)
                .build());
    }

    @Test
    public void shouldBadRequestWhenAddReviewWithoutIsPositive() throws Exception {
        Film film1 = filmService.create(film);
        User user1 = userService.create(user);

        expectBadRequestOnCreate(Review.builder()
                .filmId(film1.getId())
                .userId(user1.getId())
                .content("review")
                .build());
    }

    @Test
    public void shouldBadRequestWhenAddReviewWithoutUserId() throws Exception {
        Film film1 = filmService.create(film);

        expectBadRequestOnCreate(Review.builder()
                .filmId(film1.getId())
                .content("review")
                .isPositive(true)
                .build());
    }

    @Test
    public void shouldBadRequestWhenAddReviewWithoutFilmId() throws Exception {
        User user1 = userService.create(user);

        expectBadRequestOnCreate(Review.builder()
                .userId(user1.getId())
                .content("review")
                .isPositive(true)
                .build());
    }

    private Review createReview() {
        Film film1 = filmService.create(film);
        User user1 = userService.create(user);

        return reviewService.addReview(Review.builder()
                .content("review")
                .isPositive(true)
                .userId(user1.getId())
                .filmId(film1.getId())
                .build());
    }

    private void expectBadRequestOnCreate(Review review) throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL)
                        .content(asJsonString(review)))
                .andExpect(status().isBadRequest());
    }


}
