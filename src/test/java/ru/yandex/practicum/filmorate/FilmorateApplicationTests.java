package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    private final FilmStorage filmStorage;
    private Film film;

    private final UserStorage userStorage;
    private User user;

    @BeforeEach
    void beforeEach() {
        film = Film.builder().name("TEST").description("DESC TEST")
                .releaseDate(LocalDate.of(1957, 12, 26))
                .duration(150L)
                .mpa(Mpa
                        .builder()
                        .id(1)
                        .build())
                .genres(List.of(Genre
                        .builder()
                        .id(1)
                        .build()))
                .build();
        user = User.builder()
                .name("Vlad")
                .email("vlds@gmail.com")
                .login("spring")
                .birthday(LocalDate.of(1997, 9, 28))
                .build();

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

    @Test
    @Order(12)
    void shouldFilmWithId1WhenCreateAndFindFilm() {
        filmStorage.create(film);
        Film film = filmStorage.findFilm(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(13)
    void shouldExceptionWhenCreateCopyFilm() {
        Exception exception = assertThrows(
                Exception.class,
                () -> filmStorage.create(film));
        assertEquals("Фильм с этим названием уже существует", exception.getMessage());
    }

    @Test
    @Order(14)
    void shouldFilmsListWhenUseMethodFilms() {
        Film film2 = filmStorage.create(film.toBuilder().name("test2").description("desc test").build());
        List<Film> films = filmStorage.films();
        assertEquals(film.getName(), films.get(0).getName());
        assertEquals(film2, films.get(1));
    }

    @Test
    @Order(15)
    void shouldUpdatedUserWhetUseUpdateFilm() {
        Film updateTest = filmStorage.update(film.toBuilder().id(1L).name("Update test").build());
        assertThat(updateTest).hasFieldOrPropertyWithValue("name", "Update test");
    }

    @Test
    @Order(18)
    void shouldFilm1RateEqual1WhenUseMethodAddLike() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.addLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @Order(19)
    void shouldFilm1RateEqual1WhenUseMethodNumOfLikes() {
        List<Film> films = filmStorage.films();
        Film film1 = films.get(0);
        Integer film1rate = filmStorage.numOfLikes(film1.getId());
        assertEquals(1, film1rate);
    }

    @Test
    @Order(20)
    void shouldTopFilmsListWhenUseMethodTopFilms() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        User user2 = users.get(1);
        Film film2 = films.get(1);
        filmStorage.addLike(user2.getId(), film1.getId());
        filmStorage.addLike(user1.getId(), film2.getId());
        List<Film> topFilms = filmStorage.topFilms(10);
        assertEquals(film1.getId(), topFilms.get(0).getId());
        assertEquals(film2.getId(), topFilms.get(1).getId());
    }

    @Test
    @Order(21)
    void shouldFilm1RateEqual1WhenUseMethodRemoveLike() {
        List<Film> films = filmStorage.films();
        List<User> users = userStorage.users();
        Film film1 = films.get(0);
        User user1 = users.get(0);
        filmStorage.removeLike(user1.getId(), film1.getId());
        Film likedFilm = filmStorage.findFilm(film1.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @Order(22)
    void shouldMpaNameWhenUseMethodGetMpa() {
        Mpa mpa = filmStorage.getMpa(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @Order(23)
    void shouldAllMpaNameWhenUseMethodGetAllMpa() {
        List<Mpa> allMpa = filmStorage.getAllMpa();
        assertEquals(5, allMpa.size());
    }

    @Test
    @Order(24)
    void shouldRemoveFilm1WhenUseMethodDelete() {
        List<Film> films = filmStorage.films();
        Film film1 = films.get(0);
        filmStorage.delete(film1.getId());

    }

    @Test
    @Order(16)
    void shouldGenreNameWhenUseMethodGetGenre() {
        Genre genre = filmStorage.getGenre(1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @Order(17)
    void shouldAllGenresNameWhenUseMethodgetAllGenre() {
        List<Genre> allGenre = filmStorage.getAllGenre();
        assertEquals(6, allGenre.size());
    }
}
