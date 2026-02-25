package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Collection<Film> findAll() {

        log.info("Запрос вывода всех фильмов");

        return films.values();

    }


    @Override
    public Film create(Film film) {

        film.setId(getNextId());

        films.put(film.getId(), film);

        log.info("Создан фильм с id={}", film.getId());

        return film;

    }


    @Override
    public Film update(Film film) {

        if (!films.containsKey(film.getId())) {

            throw new NotFoundException("Фильм с id= " + film.getId() + " не найден");

        }

        films.put(film.getId(), film);

        log.info("Обновлён фильм с id={}", film.getId());

        return film;

    }


    @Override
    public Film findById(Long id) {

        Film film = films.get(id);

        if (film == null) {

            throw new NotFoundException("Фильм не найден");

        }

        return film;

    }


    private long getNextId() {

        return films.keySet().stream()

                .mapToLong(id -> id)

                .max()

                .orElse(0) + 1;

    }

}