package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

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
    }

    public void removeFriend(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Пользователь не может удалить себя из друзей.");
        }
        log.info(String.format("Пользователи c id %s и %s теперь не друзья", id1, id2));
        userStorage.deleteFriend(id1, id2);
        userStorage.setStatus(id2, id1, false);
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

}
