package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int uniqueId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        String repeatFilm = films.values().stream()
                .map(Film::getName)
                .filter(s -> s.equals(film.getName()))
                .findFirst()
                .orElse("");
        if (repeatFilm.isEmpty()) {
            Film film1 = new Film(
                    uniqueId, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration()
            );
            films.put(uniqueId, film1);
            uniqueId++;
            log.info("Добавлен новый фильм");
            return film1;
        } else {
            log.warn("Фильм с таким названием уже существует");
            throw new ValidationException("Фильм с таким названием уже существует");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм обновлен");
            return film;
        } else {
            log.warn("Фильм с таким id не найден");
            throw new ValidationException("Фильм с таким id не найден");
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
