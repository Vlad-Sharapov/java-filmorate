package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(Long id1, Long id2) {
        User user1 = userStorage.findUser(id1);
        User user2 = userStorage.findUser(id2);
        if (user1.equals(user2)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья.");
        }
        log.info(String.format("Пользователи %s и %s теперь друзья", user1.getEmail(), user2.getEmail()));
        user1.addFriend(id2);
        user2.addFriend(id1);
    }

    public void removeFriend(Long id1, Long id2) {
        User user1 = userStorage.findUser(id1);
        User user2 = userStorage.findUser(id2);
        if (user1.equals(user2)) {
            throw new ValidationException("Пользователь не может удалить себя из друзей.");
        }
        log.info(String.format("Пользователи %s и %s теперь не друзья", user1.getEmail(), user2.getEmail()));
        user1.removeFriend(id2);
        user2.removeFriend(id1);
    }

    public List<User> friends(Long id) {
        User user = userStorage.findUser(id);
        Set<Long> friendsId = user.getFriends();
        return friendsId.stream()
                .map(userId -> userStorage.getMapUsers().get(userId))
                .collect(Collectors.toList());
    }

    public List<User> commonFriends(Long id1, Long id2) {
        User user1 = userStorage.findUser(id1);
        User user2 = userStorage.findUser(id2);
        if (user1.equals(user2)) {
            throw new ValidationException("Пользователь не может посмотреть общий друзей с самим собой.");
        }
        Set<Long> user1Friends = user1.getFriends();
        Set<Long> user2Friends = user2.getFriends();
        return user1Friends.stream()
                .filter(user2Friends::contains)
                .map(userId -> userStorage.getMapUsers().get(userId))
                .collect(Collectors.toList());
    }

}
