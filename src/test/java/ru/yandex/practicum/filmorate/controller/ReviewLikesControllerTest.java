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
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ServiceTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewLikesControllerTest extends ServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    public ReviewLikesControllerTest(FilmService filmService,
                                     MpaService mpaService,
                                     GenreService genreService,
                                     UserService userService,
                                     JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void shouldLikeReview() throws Exception {
        Review review = createReview();
        User reactionUser = userService.create(user.toBuilder()
                .email("reaction@user.ru")
                .login("reactionUser")
                .build());

        mockMvc.perform(put("/reviews/" + review.getId() + "/like/" + reactionUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/" + review.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(1));
    }

    @Test
    void shouldDislikeReview() throws Exception {
        Review review = createReview();
        User reactionUser = userService.create(user.toBuilder()
                .email("reaction@user.ru")
                .login("reactionUser")
                .build());

        mockMvc.perform(put("/reviews/" + review.getId() + "/dislike/" + reactionUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/" + review.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(-1));
    }

    @Test
    void shouldDeleteReviewLike() throws Exception {
        Review review = createReview();
        User reactionUser = userService.create(user.toBuilder()
                .email("reaction@user.ru")
                .login("reactionUser")
                .build());

        mockMvc.perform(put("/reviews/" + review.getId() + "/like/" + reactionUser.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/reviews/" + review.getId() + "/like/" + reactionUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/" + review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    void shouldDeleteReviewDislike() throws Exception {
        Review review = createReview();
        User reactionUser = userService.create(user.toBuilder()
                .email("reaction@user.ru")
                .login("reactionUser")
                .build());

        mockMvc.perform(put("/reviews/" + review.getId() + "/dislike/" + reactionUser.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/reviews/" + review.getId() + "/dislike/" + reactionUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reviews/" + review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
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
