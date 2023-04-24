package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> users();

    User create(User user);

    User update(User user);

    void delete(Long id);

    List<User> getFriends(Long id);

    void addFriend(Long userId1, Long userId2);

    boolean checkFriendExist(Long userId1, Long userId2);

    void deleteFriend(Long userId1, Long userId2);

    boolean setStatus(Long userId1, Long userId2, boolean status);

    User findUserById(Long id);

    List<User> getCommonFriends(Long id1, Long id2);

    }
