package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        log.info("Запрос вывода всех фильмов");
        return films.values();
    }


    public Film create(Film film) {
        log.info("Запрос на создание фильма");
        validateFilm(film);


        film.setId(getNextId());


        films.put(film.getId(), film);
        log.info("Создан фильм с id={}", film.getId());
        return film;
    }


    public Film update(Film film) {
        Long id = film.getId();
        log.info("Запрос на обновление фильма с id={}", id);
        if (id == null) {
            throw new ValidationException("Id должен быть указан");
        }

        Film existingFilm = films.get(id);
        if (existingFilm == null) {
            log.warn("Фильм с id={} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        validateFilm(film);
        films.put(id, film);
        log.info("Обновлён фильм с id={}", id);
        return film;

    }

    private void validateFilm(Film film) {


        String name = film.getName();
        if (name == null || name.isBlank()) {
            log.warn("Ошибка валидации названия: {} - название пустое", name);
            throw new ValidationException("Название не может быть пустым");
        }


        String description = film.getDescription();
        if (description != null && description.length() > 200) {
            log.warn("Ошибка валидации описания: {} - описание длиннее 200 символов", description);
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации даты релиза: {} - дата релиза раньше 28 декабря 1895 года", releaseDate);
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (releaseDate.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации даты релиза: {} - дата релиза не может быть в будущем", releaseDate);
            throw new ValidationException("Дата релиза не может быть в будущем");
        }

        int duration = film.getDuration();
        if (duration <= 0) {
            log.warn("Ошибка валидации продолжительности фильма: {} - Продолжительность фильма не положительная", duration);
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }
}
