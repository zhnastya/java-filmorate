package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

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
        return storage.updateUser(user);
    }


    public List<User> getUsers() {
        return storage.getUsers();
    }


    public User getUserbyId(int id) {
        return storage.getUserById(id).orElseThrow();
    }


    public Set<User> addFriend(int userId, int friendId) {
        return storage.addFriend(userId, friendId);
    }


    public Set<User> removeFriend(int userId, int friendId) {
        return storage.removeFriend(userId, friendId);
    }


    public Set<User> getFriends(int id) {
        return storage.getFriends(id);
    }


    public Set<User> getSameFriends(int id, int otherId) {
        return storage.getSameFriends(id, otherId);
    }
}
