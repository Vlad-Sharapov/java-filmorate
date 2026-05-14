package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> users();

    User create(User user);

    User update(User user);

    void delete(Long id);

    List<User> friends(Long id);

    void addFriend(Long userId1, Long userId2);

    boolean checkFriendExist(Long userId1, Long userId2);

    void removeFriend(Long userId1, Long userId2);

    User findUserById(Long id);

    List<User> commonFriends(Long id1, Long id2);


}
