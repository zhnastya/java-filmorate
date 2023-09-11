package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
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

    private User getStorageUserById(Integer id) { //создала приватный метод, чтобы избежать дублирования кода
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));
    }


    public User updateUser(User user) {
        getStorageUserById(user.getId());// вызвала метод, чтобы проверить не выброситься ли исключение
        storage.updateUser(user);
        return user;
    }


    public List<User> getUsers() {
        return storage.getUsers();
    }


    public User getUserbyId(Integer id) {
        return getStorageUserById(id);
    }


    public void addFriend(Integer userId, Integer friendId) {
        User user = getStorageUserById(userId);
        User friend = getStorageUserById(friendId);
        List<User> friends = getFriends(userId);
        if (friends.isEmpty() || !friends.contains(friend)) {
            storage.addFriend(user, friend);
        }
    }


    public void removeFriend(int userId, int friendId) {
        User user = getStorageUserById(userId);
        User friend = getStorageUserById(friendId);
        List<User> friends = getFriends(userId);
        if (friends.isEmpty() || !friends.contains(friend)) return;
        storage.removeFriend(user, friend);
    }


    public List<User> getFriends(Integer id) {
        User user = getStorageUserById(id);
        return storage.getFriends(user);
    }


    public List<User> getSameFriends(int id, int otherId) {
        List<User> friendsUser = storage.getFriends(getStorageUserById(id));
        List<User> friendsOther = storage.getFriends(getStorageUserById(otherId));
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
