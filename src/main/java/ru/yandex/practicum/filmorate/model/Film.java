package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @PositiveOrZero
    private Long duration;
    private final Set<Long> usersLikeIt = new HashSet<>();

    public void addUserLike(Long userId) {
        usersLikeIt.add(userId);
    }

    public void removeUserLike(Long userId) {
        usersLikeIt.remove(userId);
    }
}
