package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;


    @Override
    public User createUser(User user) {
        return storage.createUser(user);
    }


    @Override
    public User updateUser(User user) {
        getCheckUserThrow(user.getId());
        storage.updateUser(user);
        return user;
    }


    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }


    @Override
    public User getUserById(Integer id) {
        return getCheckUserThrow(id);
    }


    private User getCheckUserThrow(Integer id) {
        return storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }


    @Override
    public void addFriend(Integer userId, Integer friendId) {
        getCheckUserThrow(userId);
        getCheckUserThrow(friendId);
        storage.addFriend(userId, friendId);
    }


    @Override
    public void removeFriend(int userId, int friendId) {
        getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
        List<User> friends = getFriends(userId);
        if (friends.isEmpty() || !friends.contains(friend)) return;
        storage.removeFriend(userId, friendId);
    }


    @Override
    public List<User> getFriends(Integer id) {
        return storage.getFriends(id);
    }


    @Override
    public List<User> getSameFriends(int id, int otherId) {
        getCheckUserThrow(id);
        getCheckUserThrow(otherId);
        List<User> friendsUser = storage.getFriends(id);
        List<User> friendsOther = storage.getFriends(otherId);
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
