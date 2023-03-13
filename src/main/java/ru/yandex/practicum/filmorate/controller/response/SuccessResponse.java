package ru.yandex.practicum.filmorate.controller.response;

import lombok.Getter;

@Getter
public class SuccessResponse {

    private final boolean success;

    public SuccessResponse(boolean success) {
        this.success = success;
    }
}
