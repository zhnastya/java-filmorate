package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    void updateUser(User user);

    List<User> getUsers();

    Optional<User> getUserById(int id);

    void addFriend(Integer userid, Integer friendId);

    void removeFriend(Integer userid, Integer friendId);

    List<User> getFriends(Integer userId);
}
