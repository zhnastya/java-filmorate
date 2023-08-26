package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int uniqueId = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        String email = users.values().stream()
                .map(User::getEmail)
                .filter(s -> s.equals(user.getEmail()))
                .findFirst()
                .orElse("");
        if (email.isEmpty()) {
            String name = user.getName() == null ? user.getLogin() : user.getName();
            User user1 = new User(
                    uniqueId, user.getEmail(), user.getLogin(), name, user.getBirthday()
            );
            users.put(uniqueId, user1);
            log.info("Добавлен новый пользователь");
            uniqueId++;
            return user1;
        } else {
            log.warn("Пользователь с таким email уже существует");
            throw new ValidationException("Пользователь с таким email уже существует");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            String name = user.getName() == null ? user.getLogin() : user.getName();
            User user1 = new User(
                    user.getId(), user.getEmail(), user.getLogin(), name, user.getBirthday()
            );
            users.put(user.getId(), user1);
            log.info("Пользователь обновлен");
            return user1;
        } else {
            log.warn("Пользователя с таким id не существует");
            throw new ValidationException("Пользователя с таким id не существует");
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
