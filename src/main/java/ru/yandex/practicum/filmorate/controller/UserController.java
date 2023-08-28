package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private int uniqueId = 1;
    private final Map<Integer, User> users = new LinkedHashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        String name = user.getName() == null ? user.getLogin() : user.getName();
        user.setName(name);
        user.setId(uniqueId);
        users.put(uniqueId, user);
        log.info("Добавлен новый пользователь");
        uniqueId++;
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId()) || user.getId() == null) {
            log.warn("Пользователя с таким id не существует");
            throw new ValidationException("Пользователя с таким id не существует");
        }
        String name = user.getName() == null ? user.getLogin() : user.getName();
        user.setName(name);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }


    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
