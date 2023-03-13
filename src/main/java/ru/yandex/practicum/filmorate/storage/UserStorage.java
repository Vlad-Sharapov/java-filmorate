package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
     List<User> users();

     User create(User user);

     User update(User user);

    Map<Long, User> getUsers();

    User findUser(Long id);

}
