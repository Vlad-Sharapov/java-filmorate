package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewLikesService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewLikesController {
    private final ReviewLikesService reviewLikesService;

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikesService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikesService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikesService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewLikesService.deleteDislike(id, userId);
    }
}
