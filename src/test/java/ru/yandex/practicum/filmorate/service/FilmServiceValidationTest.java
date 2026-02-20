package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceValidationTest {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService();
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsEmpty() {

        Film film = new Film();
        film.setName("");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при пустом name");
    }


    @Test
    void shouldThrowValidationExceptionWhenDescriptionMore200Symbols() {

        Film film = new Film();
        film.setName("name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при description больше 200 символов");
    }

    @Test
    void shouldNotThrowValidationExceptionWhenDescription200Symbols() {

        Film film = new Film();
        film.setName("name");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmService.create(film), "Не должно быть исключения при description длиной 200 символов");
    }


    @Test
    void shouldThrowValidationExceptionWhenReleaseDateEarlier28December1895() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при releaseDate раньше 28 декабря 1895");
    }

    @Test
    void shouldNotThrowValidationExceptionWhenReleaseDate28December1895() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmService.create(film), "Дата 28 декабря 1895 должна быть допустимой");
    }


    @Test
    void shouldThrowValidationExceptionWhenReleaseDateInTheFuture() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(3000, 12, 28));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при releaseDate в будущем");
    }

    @Test
    void shouldThrowValidationExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при отрицательном duration");
    }

    @Test
    void shouldThrowValidationExceptionWhenDurationIsZero() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmService.create(film), "Ожидалось исключение при duration равном нулю");
    }

    @Test
    void shouldNotThrowValidationExceptionWhenDurationIsPositive() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);

        assertDoesNotThrow(

                () -> filmService.create(film), "Duration больше нуля допустимо");
    }

}
