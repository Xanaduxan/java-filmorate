package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceValidationTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsEmpty() {

        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user), "Ожидалось исключение при пустом email");
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsIncorrect() {

        User user = new User();
        user.setEmail("mailmail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user), "Ожидалось исключение при некорректном email");
    }

    @Test
    void shouldThrowValidationExceptionWhenLoginIsEmpty() {

        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user), "Ожидалось исключение при пустом login");
    }

    @Test
    void shouldThrowValidationExceptionWhenLoginWithSpace() {

        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user), "Ожидалось исключение при пробеле в login");
    }

    @Test
    void shouldThrowValidationExceptionWhenBirthdayInFuture() {

        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(3000, 1, 1));

        assertThrows(ValidationException.class, () -> userService.create(user), "Ожидалось исключение при дате рождения в будущем");
    }

    @Test
    void shouldSetLoginAsNameWhenNameEmpty() {

        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userService.create(user);

        assertEquals("login", createdUser.getName(), "Если name пустой, должен подставляться login");
    }

}
