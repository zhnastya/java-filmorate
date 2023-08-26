package ru.yandex.practicum.filmorate.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class FilmSerializer implements JsonSerializer<Film> {

    @Override
    public JsonElement serialize(Film film, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        result.addProperty("id", film.getId());
        result.addProperty("name", film.getName());
        result.addProperty("description", film.getDescription());
        result.addProperty("releaseDate", film.getReleaseDate() != null
                ? film.getReleaseDate().format(formatter) : null);
        result.addProperty("duration", film.getDuration());
        return result;
    }
}
