package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.info("Запрос вывода всех пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Запрос на создание пользователя");


        user.setId(getNextId());


        users.put(user.getId(), user);
        log.info("Создан пользователь с id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();
        log.info("Запрос на обновление пользователя с id={}", id);

        if (id == null) {
            log.warn("У пользователя не указан id");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(id)) {
            log.warn("Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }


        users.put(id, user);
        log.info("Обновлён пользователь с id={}", id);

        return user;
    }

    @Override
    public User findById(Long id) {

        User user = users.get(id);

        if (user == null) {

            throw new NotFoundException("Пользователь с id = " + id + " не найден");

        }

        return user;

    }


    private long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }


}
