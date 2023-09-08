package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int uniqueId = 1;
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<User, Set<User>> userFriends = new HashMap<>();

    @Override
    public User createUser(User user) {
        String name = user.getName().isEmpty() ? user.getLogin() : user.getName();
        user.setName(name);
        user.setId(uniqueId);
        users.put(uniqueId, user);
        uniqueId++;
        return user;
    }


    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId()) || user.getId() == 0) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        String name = user.getName().isEmpty() ? user.getLogin() : user.getName();
        user.setName(name);
        users.put(user.getId(), user);
        return user;
    }


    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }


    @Override
    public Optional<User> getUserById(int id) {
        Optional<User> user = users.values().stream()
                .filter(user1 -> user1.getId() == id)
                .findFirst();
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }


    @Override
    public Set<User> addFriend(int userId, int friendId) {
        Set<User> friendsList;
        User user = getUserById(userId).orElseThrow();
        User friend = getUserById(friendId).orElseThrow();
        if (!userFriends.containsKey(user) || userFriends.get(user).isEmpty()) {
            friendsList = new LinkedHashSet<>();
        } else {
            friendsList = userFriends.get(user);
        }
        if (friendsList.add(friend)) {
            userFriends.put(user, friendsList);
            addFriend(friendId, userId);
        }
        return friendsList;
    }


    @Override
    public Set<User> removeFriend(int userId, int friendId) {
        Set<User> friendsList;
        User user = getUserById(userId).orElseThrow();
        User friend = getUserById(friendId).orElseThrow();
        if (!userFriends.containsKey(user) || userFriends.get(user).isEmpty()) {
            throw new NotFoundException("Список друзей пользователя пуст");
        } else {
            friendsList = userFriends.get(user);
        }
        if (friendsList.remove(friend)) {
            userFriends.put(user, friendsList);
            removeFriend(friendId, userId);
        }
        return friendsList;
    }


    @Override
    public Set<User> getFriends(int id) {
        return userFriends.get(getUserById(id).orElseThrow());
    }


    @Override
    public Set<User> getSameFriends(int id, int otherId) {
        User user = getUserById(id).orElseThrow();
        User friend = getUserById(otherId).orElseThrow();
        if (!userFriends.containsKey(user)
                || !userFriends.containsKey(friend)) {
            return Set.of();
        }
        Set<User> userFr = userFriends.get(user);
        Set<User> otherFr = userFriends.get(friend);
        Set<User> same = new LinkedHashSet<>();
        for (User user1 : userFr) {
            if (otherFr.contains(user1)) {
                same.add(user1);
            }
        }
        return same;
    }
}
