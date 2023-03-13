package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    @NotNull
    @Email
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String login;
    @Past
    private LocalDate birthday;
    private String name;
    private Set<Long> friends;


    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public void removeFriend(Long userId) {
        friends.remove(userId);
    }
}

