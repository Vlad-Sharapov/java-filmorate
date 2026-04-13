package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
class UserDbServiceTest extends ServiceTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    public UserDbServiceTest(FilmService filmService, MpaService mpaService, GenreService genreService, UserService userService, JdbcTemplate jdbcTemplate) {
        super(filmService, mpaService, genreService, userService, jdbcTemplate);
    }

    @Test
    void shouldUserWithId1WhenCreateAndFindUser() {
        User user1 = userService.create(user);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void shouldExceptionWhenCreateCopyUser() {
        userService.create(user);

        Exception exception = assertThrows(
                Exception.class,
                () -> userService.create(user));
        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
    }

    @Test
    void shouldListWhenUseMethodUsers() {
        userService.create(user);
        User user1 = userService.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());

        List<User> users = userService.users();
        assertEquals("Vlad", user.getName());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user1.getName(), users.get(1).getName());
    }

    @Test
    void shouldUpdatedUserWhetUseUpdate() {
        userService.create(user);
        User updatedUser = userService.update(user.toBuilder().id(1L).name("Updated user").login("updateLogin").build());

        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated user");
    }

    @Test
    void shouldDeletedUserWhenUseMethodDelete() {
        userService.create(user);
        userService.delete(1L);

        assertEquals(0, userService.users().size());
    }

    @Test
    void shouldCreateFriendWhenUseMethodAddFriend() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());

        List<User> users = userService.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        userService.addFriend(user1.getId(), friend1.getId());
        List<User> friends = userService.friends(1L);
        assertEquals("friend@fr.ru", friends.get(0).getEmail());
    }

    @Test
    void shouldTwoFriendsAtUserWhenCreateSecondFriendAndGetFriends() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        userService.create(user.toBuilder().email("user3@fr.ru").login("user3").build());

        List<User> users = userService.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userService.addFriend(user1.getId(), friend1.getId());
        userService.addFriend(user1.getId(), friend2.getId());
        List<User> friends = userService.friends(1L);
        assertEquals(2, friends.size());


    }

    @Test
    void shouldFriend2HaveCommonFriendWhenUseMethodGetCommonFriends() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        userService.create(user.toBuilder().email("user3@fr.ru").login("user3").build());

        List<User> users = userService.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userService.addFriend(user1.getId(), friend1.getId());
        userService.addFriend(friend2.getId(), friend1.getId());
        List<User> commonFriends = userService.commonFriends(friend2.getId(), user1.getId());
        assertEquals(friend1, commonFriends.get(0));
    }

    @Test
    void shouldRemoveFriend1FromFriendsUser1WhenUseMethodDeleteFriend() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        userService.create(user.toBuilder().email("user3@fr.ru").login("user3").build());

        List<User> users = userService.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userService.addFriend(user1.getId(), friend1.getId());
        userService.addFriend(user1.getId(), friend2.getId());
        userService.removeFriend(user1.getId(), friend1.getId());
        List<User> friends = userService.friends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(friend2, friends.get(0));
    }

    @Test
    void shouldReturnTrueWhenUseMethodCheckFriendExist() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());

        List<User> users = userService.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        userService.addFriend(user1.getId(), friend1.getId());

        boolean flag = userService.checkFriendExist(user1.getId(), friend1.getId());
        assertTrue(flag);
    }

    @Test
    void shouldReturnTrueWhenUser1AndFriend1Friends() {
        userService.create(user);
        userService.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        List<User> users = userService.users();
        User user1 = users.get(0);
        User user2 = users.get(1);
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user2.getId(), user1.getId());

        boolean friendsStatus = userDbStorage.getFriendsStatus(user1.getId(), user2.getId());

        assertTrue(friendsStatus);
    }

    @Test
    void shouldThrowExceptionWhenUser1AddHimself() {
        userService.create(user);
        User user1 = userService.findUserById(1L);
        assertThrows(ValidationException.class, () -> {
            userService.addFriend(user1.getId(), user1.getId());
        });
    }
}
