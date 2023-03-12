package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer userId = 0;

    @GetMapping
    public List<User> users() {
        log.info("Количество пользователей: {}", users.values().size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        int id = incrementUserId();
        checkValidation(user);
        changeEmptyName(user);
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User saveUser = users.get(user.getId());
        if (saveUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        checkValidation(user);
        users.put(user.getId(), user);
        log.info("Фильм изменен с {} на {}", saveUser, user);
        return user;
    }

    private void checkValidation(User saveUser) {
        if (saveUser.getLogin().contains(" ")) {
            log.warn("Некорректные данные (Аргумент параметра \"login\" имеет пробелы).");
            throw new ArgumentNotValidException();
        }
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private int incrementUserId() {
        return ++userId;
    }

}
