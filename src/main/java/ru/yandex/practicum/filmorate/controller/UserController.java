package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping
public class UserController {
    private final UserService service;
    private final UserStorage storage;

    @Autowired
    public UserController(UserService service, UserStorage storage) {
        this.service = service;
        this.storage = storage;
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return storage.createUser(user);
    }


    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return storage.updateUser(user);
    }


    @GetMapping("/users")
    public List<User> getUsers() {
        return storage.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        if (id < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return storage.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public Set<User> addFriend(@PathVariable("id") Integer id,
                               @PathVariable("friendId") Integer friendId) {
        if (id < 0 || friendId < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Set<User> deleteFriend(@PathVariable Integer id,
                                  @PathVariable Integer friendId) {
        if (id < 0 || friendId < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> getFriends(@PathVariable Integer id) {
        if (id < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getSameFriends(@PathVariable Integer id,
                                    @PathVariable Integer otherId) {
        if (id < 0 || otherId < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.getSameFriends(id, otherId);
    }
}
