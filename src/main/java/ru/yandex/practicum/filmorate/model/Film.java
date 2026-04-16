package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Film {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма должно быть больше либо равно 0")
    private Long duration;

    private Mpa mpa;

    private List<Genre> genres;
    private Integer rate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Director> directors;

}

