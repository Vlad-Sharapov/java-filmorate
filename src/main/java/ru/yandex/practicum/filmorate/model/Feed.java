package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class Feed {

    @JsonProperty("eventId")
    private Long id;
    @NotBlank
    private Long userId;
    @NotBlank
    private Long entityId;
    @NotBlank
    private Long timestamp;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
}
