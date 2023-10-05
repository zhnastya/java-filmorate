package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {
    private final UserDbStorage userStorage;

    @BeforeEach
    public void createParams(){
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userStorage.createUser(user);
    }

    @Test
    public void shouldUpdate(){
        User user = new User(1, "email@email.ru", "new", "new",
                LocalDate.of(2001, 1, 1));
        userStorage.updateUser(user);

        assertEquals(user, userStorage.getUserById(1).orElseThrow());
    }

    @Test
    public void shouldGetAllUsers(){
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    public void shouldGetUserById(){
        User user = userStorage.getUserById(1).orElse(null);

        assertNotNull(user);
        assertEquals(user.getName(), "name");
    }

    @Test
    public void shouldAddFriend(){
        User friend = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userStorage.createUser(friend);
        userStorage.addFriend(1, 2);

        assertEquals(userStorage.getFriends(2).size(), 0);
        assertEquals(userStorage.getFriends(1).size(), 1);
        assertEquals(userStorage.getFriends(1).get(0), userStorage.getUserById(2).orElseThrow());
    }

    @Test
    public void shouldRemoveFriend(){
        User friend = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userStorage.createUser(friend);
        userStorage.addFriend(1, 2);
        userStorage.removeFriend(1, 2);

        assertEquals(userStorage.getFriends(2).size(), 0);
        assertEquals(userStorage.getFriends(1).size(), 0);
    }
}
