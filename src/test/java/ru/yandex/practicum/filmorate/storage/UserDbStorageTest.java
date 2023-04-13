package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest extends StorageTest {

    @Autowired
    public UserDbStorageTest(FilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage, UserStorage userStorage) {
        super(filmStorage, mpaStorage, genreStorage, userStorage);
    }

    @Test
    @Order(1)
    void shouldUserWithId1WhenCreateAndFindUser() {
        User user1 = userStorage.create(user);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(2)
    void shouldExceptionWhenCreateCopyUser() {
        Exception exception = assertThrows(
                Exception.class,
                () -> userStorage.create(user));
        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
    }

    @Test
    @Order(3)
    void shouldListWhenUseMethodUsers() {
        User user1 = userStorage.create(user.toBuilder().login("test").email("qwerty@qwqerty.ru").build());
        List<User> users = userStorage.users();
        assertEquals("Vlad", user.getName());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user1.getName(), users.get(1).getName());
    }

    @Test
    @Order(4)
    void shouldUpdatedUserWhetUseUpdate() {
        User updatedUser = userStorage.update(user.toBuilder().id(1L).name("Updated user").login("updateLogin").build());
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", "Updated user");
    }

    @Test
    @Order(5)
    void shouldDeletedUserWhenUseMethodDelete() {
        userStorage.delete(2L);
        assertEquals(1, userStorage.users().size());
    }

    @Test
    @Order(6)
    void shouldCreateFriendWhenUseMethodAddFriend() {
        userStorage.create(user.toBuilder().email("friend@fr.ru").login("friend").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        userStorage.addFriend(user1.getId(), friend1.getId());
        List<User> friends = userStorage.getFriends(1L);
        assertEquals("friend@fr.ru", friends.get(0).getEmail());
    }

    @Test
    @Order(7)
    void shouldTwoFriendsAtUserWhenCreateSecondFriendAndGetFriends() {
        userStorage.create(user.toBuilder().email("user3@fr.ru").login("user3").build());
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(2);
        userStorage.addFriend(user1.getId(), friend1.getId());
        List<User> friends = userStorage.getFriends(1L);
        assertEquals(2, friends.size());


    }

    @Test
    @Order(8)
    void shouldFriend2CommonFriendWhenUseMethodGetCommonFriends() {
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userStorage.addFriend(friend1.getId(), friend2.getId());
        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), friend1.getId());
        assertEquals(friend2, commonFriends.get(0));
    }

    @Test
    @Order(9)
    void shouldRemoveFriend1FromFriendsUser1WhenUseMethodDeleteFriend() {
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(1);
        User friend2 = users.get(2);
        userStorage.deleteFriend(user1.getId(), friend1.getId());
        List<User> friends = userStorage.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(friend2, friends.get(0));
    }

    @Test
    @Order(10)
    void shouldReturnTrueWhenUseMethodCheckFriendExist() {
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend2 = users.get(2);
        boolean flag = userStorage.checkFriendExist(user1.getId(), friend2.getId());
        assertTrue(flag);
    }

    @Test
    @Order(11)
    void shouldReturnTrueWhenUseMethodSetStatus() {
        List<User> users = userStorage.users();
        User user1 = users.get(0);
        User friend1 = users.get(2);
        boolean flag = userStorage.setStatus(user1.getId(), friend1.getId(), true);
        assertTrue(flag);
    }

}