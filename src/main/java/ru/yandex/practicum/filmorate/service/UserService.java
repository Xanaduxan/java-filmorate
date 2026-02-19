package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();


    public Collection<User> findAll() {
        return users.values();
    }


    public User create(User user) {
        log.info("Запрос на создание пользователя");
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Логин задан в качестве имени");
            user.setName(user.getLogin());
        }


        user.setId(getNextId());


        users.put(user.getId(), user);
        log.info("Создан пользователь с id={}", user.getId());
        return user;
    }


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

        validateUser(user);

        String name = user.getName();
        if (name == null || name.isBlank()) {
            log.info("Логин задан в качестве имени у пользователя с id={}", id);
            user.setName(user.getLogin());
        }

        users.put(id, user);
        log.info("Обновлён пользователь с id={}", id);

        return user;
    }


    private void validateUser(User user) {


        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Ошибка валидации email: {} - пустая почта", email);
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!email.contains("@")) {
            log.warn("Ошибка валидации email: {} - отсутствие символа @", email);
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        String login = user.getLogin();
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn("Ошибка валидации логина: {} - логин пустой или содержит пробелы", login);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        LocalDate birthday = user.getBirthday();
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации даты рождения: {} - дата рождения в будущем", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }
}
