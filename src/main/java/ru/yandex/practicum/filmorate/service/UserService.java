package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public Set<User> addFriend(int userId, int friendId) {
        Set<User> friendsList;
        User friend = storage.getUserById(friendId);
        if (storage.getFriendsById(userId).isEmpty()) {
            friendsList = new HashSet<>();
        } else {
            friendsList = storage.getFriendsById(userId);
        }
        if (!friendsList.add(friend)) {
            return friendsList;
        }
        storage.updateFriend(userId, friendsList);
        addFriend(friendId, userId);
        log.info("Пользователи добавлены в друзья");
        return friendsList;
    }

    public Set<User> removeFriend(int userId, int friendId) {
        Set<User> friendsList;
        User friend = storage.getUserById(friendId);
        if (storage.getFriendsById(userId).isEmpty()) {
            friendsList = new HashSet<>();
        } else {
            friendsList = storage.getFriendsById(userId);
        }
        if (!friendsList.remove(friend)) {
            return friendsList;
        }
        friendsList.remove(friend);
        storage.updateFriend(userId, friendsList);
        removeFriend(friendId, userId);
        log.info("Пользователи удалены из друзей");
        return friendsList;
    }

    public Set<User> getFriends(int id) {
        return storage.getFriendsById(id);
    }

    public Set<User> getSameFriends(int id, int otherId) {
        Set<User> userFr = storage.getFriendsById(id);
        Set<User> otherFr = storage.getFriendsById(otherId);
        if (userFr.isEmpty() || otherFr.isEmpty()) {
            log.info("Список друзей пуст");
            return Set.of();
        }
        Set<User> same = new HashSet<>();
        for (User user : userFr) {
            if (otherFr.contains(user)) {
                same.add(user);
            }
        }
        log.info("Отправлен список общих друзей у пользователей с id - " + id + " и " + otherId);
        return same;
    }
}
