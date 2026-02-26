package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Запрос вывода всех пользователей");
        return userStorage.findAll();
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя");
        validateUser(user);
        fillNameIfBlank(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        log.info("Запрос на обновление пользователя с id={}", user.getId());
        validateUser(user);
        fillNameIfBlank(user);
        return userStorage.update(user);
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья: id={}, id={}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);


        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователи id={} и id={} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей: id={}, id={}", userId, friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя удалить самого себя из своих друзей");
        }
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));


        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи id={} и id={} больше не друзья", userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Запрос общих друзей: id={}, id={}", userId, otherId);
        if (userId.equals(otherId)) {
            throw new ValidationException("Id не должны быть одинаковыми");
        }
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        User otherUser = userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + otherId + " не найден"));

        Set<Long> commonFriendsIds = new HashSet<>(user.getFriends());
        commonFriendsIds.retainAll(otherUser.getFriends());

        Collection<User> commonFriends = commonFriendsIds.stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден")))
                .toList();

        log.info("Общие друзья для id={} и id={} успешно получены", userId, otherId);
        return commonFriends;
    }

    public Collection<User> getFriends(Long userId) {
        log.info("Запрос списка друзей: id={}", userId);

        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Collection<User> friends = user.getFriends().stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден")))
                .toList();

        log.info("Список друзей для id={} успешно получен", userId);
        return friends;
    }


    private void validateUser(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Электронная почта должна содержать символ @");
        }

        String login = user.getLogin();
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        LocalDate birthday = user.getBirthday();
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void fillNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}