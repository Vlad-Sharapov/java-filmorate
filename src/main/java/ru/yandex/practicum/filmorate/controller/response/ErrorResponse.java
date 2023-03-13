package ru.yandex.practicum.filmorate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;

    private String error;

}
