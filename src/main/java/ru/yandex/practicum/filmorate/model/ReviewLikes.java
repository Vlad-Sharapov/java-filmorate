package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewLikes {
    private int reviewId;
    private int userId;
    private boolean isDislike;
}
