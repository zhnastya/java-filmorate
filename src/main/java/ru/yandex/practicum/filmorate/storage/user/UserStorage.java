package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(int id);

    Set<User> getFriendsById(int id);

    void updateFriend(int id, Set<User> users);
}
