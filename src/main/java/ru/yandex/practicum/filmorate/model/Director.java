package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class Director {

    private Long id;

    @NotBlank(message = "Имя режессера не может быть пустым")
    @Size(max = 50)
    private String name;

}
