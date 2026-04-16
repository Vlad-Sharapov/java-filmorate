package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.utils.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.utils.EventType.LIKE;
import static ru.yandex.practicum.filmorate.utils.Operation.ADD;
import static ru.yandex.practicum.filmorate.utils.Operation.REMOVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    private final FeedStorage feedStorage;

    @Override
    public User create(User user) {
        if (userStorage.checkUserExist(user.getEmail()))
            throw new ValidationException("Пользователь с такой почтой уже существует");
        entityValidation(user);
        changeEmptyName(user);
        Long id = userStorage.create(user);
        return user.toBuilder()
                .id(id)
                .build();
    }

    @Override
    public User update(User user) {
        if (!userStorage.checkUserExist(user.getId())) {
            log.warn("Пользователь с id - {} не найден", user.getEmail());
            throw new UserNotFoundException(String.format("Пользователь %s не найден", user.getEmail()));
        }
        entityValidation(user);
        changeEmptyName(user);
        userStorage.update(user);
        log.info("Пользователь обновлен: {}", user);
        return user.toBuilder()
                .build();
    }

    @Override
    public void delete(Long id) {
        userStorage.findUserById(id);
        userStorage.delete(id);
    }

    public void addFriend(Long id1, Long id2) {
        userStorage.findUserById(id1);
        userStorage.findUserById(id2);
        if (id1.equals(id2)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья.");
        }
        if (userStorage.checkFriendExist(id1, id2)) {
            log.info(String.format("У пользователя c id %s уже есть друг c id %s.", id1, id2));
            return;
        }
        userStorage.addFriend(id1, id2);
        if (userStorage.checkFriendExist(id2, id1)) {
            userStorage.setStatus(id2, id1, true);
            userStorage.setStatus(id1, id2, true);
            log.info(String.format("Пользователи c id %s и %s теперь друзья.", id1, id2));
        }
        feedStorage.addFeed(id2, id1, Instant.now().toEpochMilli(), FRIEND, ADD);

    }

    @Override
    public boolean checkFriendExist(Long userId1, Long userId2) {
        return userStorage.checkFriendExist(userId1, userId2);
    }

    public void removeFriend(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Пользователь не может удалить себя из друзей.");
        }
        log.info(String.format("Пользователи c id %s и %s теперь не друзья", id1, id2));
        userStorage.deleteFriend(id1, id2);
        userStorage.setStatus(id2, id1, false);
        feedStorage.addFeed(id2, id1, Instant.now().toEpochMilli(), LIKE, REMOVE);
    }

    @Override
    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    @Override
    public List<User> users() {
        return userStorage.users();
    }

    public List<User> friends(Long id) {
        userStorage.findUserById(id);
        return userStorage.getFriends(id);
    }

    public List<User> commonFriends(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Пользователь не может посмотреть общий друзей с самим собой.");
        }
        return userStorage.getCommonFriends(id1, id2);
    }


    private void entityValidation(User saveUser) {
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

}
