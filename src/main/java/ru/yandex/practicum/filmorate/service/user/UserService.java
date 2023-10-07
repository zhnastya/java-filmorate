package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(Integer id);

    List<User> getSameFriends(int id, int otherId);
}
