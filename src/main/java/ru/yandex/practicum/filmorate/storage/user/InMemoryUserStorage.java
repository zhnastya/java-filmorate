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
    private final Map<Integer, Set<User>> friends = new HashMap<>();

    @Override
    public void updateFriend(int id, Set<User> user) {
        friends.put(id, user);
    }

    @Override
    public Set<User> getFriendsById(int id) {
        if (!friends.containsKey(id)) {
            return Set.of();
        }
        return friends.get(id);
    }

    @Override
    public User createUser(User user) {
        String name = user.getName().isEmpty() ? user.getLogin() : user.getName();
        user.setName(name);
        user.setId(uniqueId);
        users.put(uniqueId, user);
        uniqueId++;
        log.info("Пользователь создан");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId()) || user.getId() == null) {
            log.warn("Ошибка обновления пользователя " + InMemoryUserStorage.class.getSimpleName());
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        String name = user.getName().isEmpty() ? user.getLogin() : user.getName();
        user.setName(name);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.values().stream()
                .filter(user1 -> user1.getId() == id)
                .findFirst()
                .orElse(null);
        if (user == null) {
            log.warn("Ошибка получения пользователя " + InMemoryUserStorage.class.getSimpleName());
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }
}
