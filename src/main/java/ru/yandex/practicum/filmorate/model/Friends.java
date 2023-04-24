package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friends {

    private final int id;
    private final int user;
    private final int friend;
    private final boolean status;

}
