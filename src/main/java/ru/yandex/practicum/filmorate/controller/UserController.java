package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя");
        User user1 = service.createUser(user);
        log.info("Пользователь создан");
        return user1;

    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя - " + user.getId());
        User user1 = service.updateUser(user);
        log.info("Пользователь " + user1.getId() + " обновлен");
        return user1;
    }


    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос на получение списка всех пользователей");
        List<User> users = service.getUsers();
        log.info("Список всех пользователей отправлен");
        return users;
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") int id) {
        log.info("Запрос на получение пользователя с id - " + id);
        User user = service.getUserbyId(id);
        log.info("Пользователь с id - " + id + " отправлен");
        return user;
    }


    @PutMapping("/{id}/friends/{friendId}")
    public Set<User> addFriend(@PathVariable("id") int id,
                               @PathVariable("friendId") int friendId) {
        log.info("Запрос на добавление в друзья пользователей: " + id + " и " + friendId);
        Set<User> friends = service.addFriend(id, friendId);
        log.info("Пользователи: " + id + " и " + friendId + " добавлены в друзья");
        return friends;
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public Set<User> deleteFriend(@PathVariable int id,
                                  @PathVariable int friendId) {
        log.info("Запрос на удаление из друзей пользователей: " + id + " и " + friendId);
        Set<User> friends = service.removeFriend(id, friendId);
        log.info("Пользователи: " + id + " и " + friendId + " удалены из друзей");
        return friends;
    }


    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable int id) {
        log.info("Запрос на получение друзей пользователя - " + id);
        Set<User> friends = service.getFriends(id);
        log.info("Отправлен список друзей пользователя - " + id);
        return friends;
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getSameFriends(@PathVariable int id,
                                    @PathVariable int otherId) {
        log.info("Запрос на получение общих друзей пользователей: " + id + " и " + otherId);
        Set<User> friends = service.getSameFriends(id, otherId);
        log.info("Отправлен список общих друзей пользователей: " + id + " и " + otherId);
        return friends;
    }
}
