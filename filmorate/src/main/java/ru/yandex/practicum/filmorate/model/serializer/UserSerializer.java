package ru.yandex.practicum.filmorate.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        result.addProperty("id", user.getId());
        result.addProperty("email", user.getEmail());
        result.addProperty("login", user.getLogin());
        result.addProperty("name", user.getName());
        result.addProperty("birthday", user.getBirthday() != null ? user.getBirthday().format(formatter) : null);
        return result;
    }
}
