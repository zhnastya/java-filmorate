package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int uniqueId = 1;
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<User, List<User>> userFriends = new HashMap<>();


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
        return Optional.ofNullable(users.get(id));
    }


    @Override
    public void addFriend(User user, User friend) {
        List<User> friendsList = userFriends.containsKey(user) ? userFriends.get(user) : new ArrayList<>();
        if (!friendsList.contains(friend)) {
            friendsList.add(friend);
            userFriends.put(user, friendsList);
            addFriend(friend, user);
        }
    }


    @Override
    public void removeFriend(User user, User friend) {
        List<User> friendsList = userFriends.get(user);
        if (friendsList.remove(friend)) {
            userFriends.put(user, friendsList);
            removeFriend(friend, user);
        }
    }


    @Override
    public List<User> getFriends(User user) {
        return userFriends.containsKey(user) ? userFriends.get(user) : new ArrayList<>();
    }
}
