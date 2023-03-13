package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.response.SuccessResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping()
    public List<User> users() {
        return userStorage.users();
    }

    @GetMapping("/{id}")
    public User users(@PathVariable Long id) {
        return userStorage.findUser(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public SuccessResponse addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        return new SuccessResponse(true);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public SuccessResponse removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        return new SuccessResponse(true);
    }

    @GetMapping("/{id}/friends")
    public List<User> friends(@PathVariable Long id) {
        return userService.friends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }
}
