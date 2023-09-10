package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.ValidatorService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }


    public User createUser(User user) {
        return storage.createUser(user);
    }


    public User updateUser(User user) {
        getUserbyId(user.getId());
        return storage.updateUser(user);
    }


    public List<User> getUsers() {
        return storage.getUsers();
    }


    public User getUserbyId(Integer id) {
        ValidatorService.validateId(id);
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));
    }


    public List<User> addFriend(Integer userId, Integer friendId) {
        User user = getUserbyId(userId);
        User friend = getUserbyId(friendId);
        List<User> friends = getFriends(userId);
        if (friends.isEmpty() || !friends.contains(friend)) {
            storage.addFriend(user, friend);
        }
        return getFriends(userId);
    }


    public List<User> removeFriend(int userId, int friendId) {
        User user = getUserbyId(userId);
        User friend = getUserbyId(friendId);
        List<User> friends = getFriends(userId);
        if (friends.isEmpty() || !friends.contains(friend)) {
            throw new NotFoundException("Список друзей пользователя - " + userId + " пуст");
        }
        storage.removeFriend(user, friend);
        return getFriends(userId);
    }


    public List<User> getFriends(Integer id) {
        User user = getUserbyId(id);
        return storage.getFriends(user);
    }


    public List<User> getSameFriends(int id, int otherId) {
        List<User> friendsUser = getFriends(id);
        List<User> friendsOther = getFriends(otherId);
        List<User> sameUser = new ArrayList<>();
        if (friendsUser.isEmpty() || friendsOther.isEmpty()) {
            return sameUser;
        }
        for (User user : friendsUser) {
            if (friendsOther.contains(user)) {
                sameUser.add(user);
            }
        }
        return sameUser;
    }
}
