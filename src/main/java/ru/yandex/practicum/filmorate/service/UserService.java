package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;
    private final Map<Integer, Set<User>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }


    public Set<User> addFriend(int userId, int friendId) {
        Set<User> friendsList;
        User friend = storage.getUserById(friendId);
        if (!friends.containsKey(userId) || friends.get(userId).isEmpty()) {
            friendsList = new LinkedHashSet<>();
        } else {
            friendsList = friends.get(userId);
        }
        if (friendsList.add(friend)) {
            friends.put(userId, friendsList);
            addFriend(friendId, userId);
            log.info("Пользователи " + userId + " и " + friendId + " добавлены в друзья");
        }
        return friendsList;
    }

    public Set<User> removeFriend(int userId, int friendId) {
        Set<User> friendsList;
        User friend = storage.getUserById(friendId);
        if (!friends.containsKey(userId) || friends.get(userId).isEmpty()) {
            throw new NotFoundException("Список друзей пользователя пуст");
        } else {
            friendsList = friends.get(userId);
        }
        if (friendsList.remove(friend)) {
            friends.put(userId, friendsList);
            removeFriend(friendId, userId);
            log.info("Пользователи " + userId + " и " + friendId + " удалены из друзей");
        }
        return friendsList;
    }

    public Set<User> getFriends(int id) {
        return friends.get(id);
    }

    public Set<User> getSameFriends(int id, int otherId) {
        if (!friends.containsKey(id) || !friends.containsKey(otherId)) {
            return Set.of();
        }
        Set<User> userFr = friends.get(id);
        Set<User> otherFr = friends.get(otherId);
        Set<User> same = new LinkedHashSet<>();
        for (User user : userFr) {
            if (otherFr.contains(user)) {
                same.add(user);
            }
        }
        log.info("Отправлен список общих друзей у пользователей с id - " + id + " и " + otherId);
        return same;
    }
}
