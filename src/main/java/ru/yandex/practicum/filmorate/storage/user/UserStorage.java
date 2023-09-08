package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    Optional<User> getUserById(int id);

    Set<User> addFriend(int userId, int friendId);

    Set<User> removeFriend(int userId, int friendId);

    Set<User> getFriends(int id);

    Set<User> getSameFriends(int id, int otherId);
}
