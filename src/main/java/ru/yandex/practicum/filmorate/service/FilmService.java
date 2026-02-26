package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        log.info("Запрос вывода всех фильмов");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        log.info("Запрос на создание фильма");
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Запрос на обновление фильма с id={}", film.getId());
        filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + film.getId() + " не найден"));
        validateFilm(film);
        return filmStorage.update(film);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка пользователем с id={} к фильму с id={}", userId, filmId);

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        film.getLikes().add(userId);
        filmStorage.update(film);

        log.info("Лайк к фильму с id={} от пользователя с id={} добавлен", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка пользователя с id={} к фильму с id={}", userId, filmId);

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        film.getLikes().remove(userId);
        filmStorage.update(film);

        log.info("Лайк к фильму с id={} от пользователя с id={} удален", filmId, userId);
    }

    public Collection<Film> getPopular(int count) {
        log.info("Запрос популярных фильмов: count={}", count);

        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным");
        }

        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        String name = film.getName();

        if (name == null || name.isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        String description = film.getDescription();

        if (description != null && description.length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        LocalDate releaseDate = film.getReleaseDate();

        if (releaseDate == null ||
                releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (releaseDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата релиза не может быть в будущем");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}