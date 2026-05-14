package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDbStorageTest extends StorageTest {

    @Autowired
    public UserDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        super(filmStorage, mpaStorage, genreStorage, userStorage, jdbcTemplate);
    }

    @Test
    void shouldUserWithId1WhenCreateAndFindUser() {
       Long userId = userStorage.create(user);
        assertThat(userId).isEqualTo(1L);
    }


    @Test
    void shouldListWhenUseMethodUsers() {
        userStorage.create(user);
        Long user1Id = userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        List<User> users = userStorage.users();
        assertEquals("Vlad", user.getName());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user1Id, users.get(1).getId());
    }

    @Test
    void shouldUpdatedUserWhetUseUpdate() {
        User updatedUser = userStorage.update(user.toBuilder().id(1L).name("Updated user").login("updateLogin").build());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated user");
    }

    @Test
    void shouldDeletedUserWhenUseMethodDelete() {
        userStorage.create(user);
        userStorage.delete(1L);
        assertEquals(0, userStorage.users().size());
    }

    @Test
    void shouldCreateFriendWhenUseMethodAddFriend() {
        userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        userStorage.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        userStorage.addFriend(user1.getId(), friend1.getId());
        List<User> friends = userStorage.getFriends(1L);
        assertEquals("friend@fr.ru", friends.get(0).getEmail());
    }

    @Test
    void shouldTwoFriendsAtUserWhenCreateSecondFriendAndGetFriends() {
        userStorage.create(user);
        userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        userStorage.create(user.toBuilder().email("user3@fr.ru").login("user3").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userStorage.addFriend(user1.getId(), friend1.getId());
        userStorage.addFriend(user1.getId(), friend2.getId());
        List<User> friends = userStorage.getFriends(1L);
        assertEquals(2, friends.size());


    }

    @Test
    void shouldFriend2CommonFriendWhenUseMethodGetCommonFriends() {
        userStorage.create(user);
        userStorage.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        userStorage.create(user.toBuilder().email("user3@fr.ru").login("user3").build());

        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userStorage.addFriend(friend1.getId(), friend2.getId());
        userStorage.addFriend(user1.getId(), friend2.getId());
        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), friend1.getId());
        assertEquals(friend2, commonFriends.get(0));
    }

    @Test
    void shouldRemoveFriend1FromFriendsUser1WhenUseMethodDeleteFriend() {
        userStorage.create(user);
        userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        userStorage.create(user.toBuilder().email("user3@fr.ru").login("user3").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userStorage.addFriend(user1.getId(), friend2.getId());
        userStorage.addFriend(user1.getId(), friend1.getId());
        userStorage.deleteFriend(user1.getId(), friend1.getId());
        List<User> friends = userStorage.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(friend2, friends.get(0));
    }

    @Test
    void shouldReturnTrueWhenUseMethodCheckFriendExist() {
        userStorage.create(user);
        userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend2 = users.get(1);
        userStorage.addFriend(user1.getId(), friend2.getId());
        boolean flag = userStorage.checkFriendExist(user1.getId(), friend2.getId());
        assertTrue(flag);
    }

    @Test
    void shouldReturnTrueWhenUseMethodSetStatus() {
        userStorage.create(user);
        userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        boolean flag = userStorage.setStatus(user1.getId(), friend1.getId(), true);
        assertTrue(flag);
    }

}
