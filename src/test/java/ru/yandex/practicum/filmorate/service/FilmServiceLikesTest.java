package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceLikesTest {

    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    private User newUser(String email, String login) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return userStorage.create(u);
    }

    private Film newFilm(String name) {
        Film f = new Film();
        f.setName(name);
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(120);
        return filmService.create(f);
    }

    @Test
    void shouldAddLikeOnceWhenAddLikeTwice() {
        User u = newUser("mail1@example.com", "user1");
        Film f = newFilm("Film");

        filmService.addLike(f.getId(), u.getId());
        filmService.addLike(f.getId(), u.getId());

        Film updated = filmService.findById(f.getId());
        assertEquals(1, updated.getLikes().size());
    }

    @Test
    void shouldRemoveLikeWhenRemoveLike() {
        User u = newUser("mail1@example.com", "user1");
        Film f = newFilm("Film");

        filmService.addLike(f.getId(), u.getId());
        filmService.removeLike(f.getId(), u.getId());

        Film updated = filmService.findById(f.getId());
        assertEquals(0, updated.getLikes().size());
    }

    @Test
    void shouldReturnFilmsSortedByLikesWhenGetPopular() {
        User u1 = newUser("mail1@example.com", "user1");
        User u2 = newUser("mail2@example.com", "user2");

        Film f1 = newFilm("F1");
        Film f2 = newFilm("F2");
        Film f3 = newFilm("F3");

        filmService.addLike(f2.getId(), u1.getId());
        filmService.addLike(f2.getId(), u2.getId());
        filmService.addLike(f1.getId(), u1.getId());

        List<Film> popular = filmService.getPopular(10).stream().toList();

        assertEquals(List.of(f2.getId(), f1.getId(), f3.getId()),
                popular.stream().map(Film::getId).toList());
    }

    @Test
    void shouldThrowValidationExceptionWhenGetPopularWithInvalidCount() {
        assertThrows(ValidationException.class, () -> filmService.getPopular(0));
        assertThrows(ValidationException.class, () -> filmService.getPopular(-1));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenAddLikeWithMissingUser() {
        Film f = newFilm("Film");
        assertThrows(NotFoundException.class, () -> filmService.addLike(f.getId(), 999L));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenAddLikeWithMissingFilm() {
        User u = newUser("mail1@example.com", "user1");
        assertThrows(NotFoundException.class, () -> filmService.addLike(999L, u.getId()));
    }

    @Test
    void shouldThrowValidationExceptionWhenCountIsNegativeInGetPopularFilms() {
        assertThrows(
                ValidationException.class,
                () -> filmService.getPopular(-1)
        );
    }

    @Test
    void shouldThrowValidationExceptionWhenCountIsZeroInGetPopularFilms() {
        assertThrows(
                ValidationException.class,
                () -> filmService.getPopular(0)
        );
    }

    @Test
    void shouldReturnFilmWithMostLikesWhenGetPopularFilms() {
        User user = new User();
        user.setEmail("mail1@example.com");
        user.setLogin("user1");
        user.setName("User1");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user = userStorage.create(user);

        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("desc");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        film1 = filmService.create(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("desc");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(100);


        filmService.addLike(film1.getId(), user.getId());

        Film result = filmService.getPopular(1).iterator().next();

        assertEquals(film1.getId(), result.getId());
        assertEquals(1, result.getLikes().size());
    }
}