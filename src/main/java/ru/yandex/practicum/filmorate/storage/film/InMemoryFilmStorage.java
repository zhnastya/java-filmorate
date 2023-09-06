package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int uniqueId = 1;
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @Override
    public Film createFilm(Film film) {
        film.setId(uniqueId);
        films.put(uniqueId, film);
        uniqueId++;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId()) || film.getId() == null) {
            log.warn("Ошибка обновления фильма " + InMemoryFilmStorage.class.getSimpleName());
            throw new NotFoundException("Фильм с таким id не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.values().stream()
                .filter(film1 -> film1.getId() == id)
                .findFirst()
                .orElse(null);
        if (film == null) {
            log.warn("Ошибка получения фильма " + InMemoryFilmStorage.class.getSimpleName());
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }
}
