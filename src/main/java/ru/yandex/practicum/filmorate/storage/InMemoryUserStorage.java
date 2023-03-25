package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.GeneratorId;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final GeneratorId generatorUserId;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> users() {
        log.info("Количество пользователей: {}", users.values().size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        Long id = generatorUserId.incrementId();
        checkValidation(user);
        changeEmptyName(user);
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        User saveUser = users.get(user.getId());
        if (saveUser == null) {
            log.warn(String.format("Фильм с id - %s не найден", user.getEmail()));
            throw new UserNotFoundException(String.format("Пользователь %s не найден", user.getEmail()));
        }
        checkValidation(user);
        users.put(user.getId(), user);
        log.info("Фильм изменен с {} на {}", saveUser, user);
        return user;
    }

    private void checkValidation(User saveUser) {
        if (saveUser.getLogin().contains(" ")) {
            log.warn("Некорректные данные (Аргумент параметра \"login\" имеет пробелы).");
            throw new ValidationException();
        }
    }

    private void changeEmptyName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public Map<Long, User> getMapUsers() {
        return users;
    }

    @Override
    public User findUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn(String.format("Фильм с id - %s не найден", id));
            throw new UserNotFoundException(String.format("Пользователь с id - %s не найден", id));
        }
        return user;
    }
}
