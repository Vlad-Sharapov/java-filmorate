package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.GeneratorId;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final GeneratorId generatorUserId;
    private final Map<Long, User> users = new HashMap<>();

    @Autowired
    public InMemoryUserStorage(GeneratorId generatorUserId) {
        this.generatorUserId = generatorUserId;
    }


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
        user.setFriends(new HashSet<>());
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
        if (user.getFriends() == null) {
            user.setFriends(saveUser.getFriends());
        }
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

    @Override
    public Map<Long, User> getUsers() {
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
