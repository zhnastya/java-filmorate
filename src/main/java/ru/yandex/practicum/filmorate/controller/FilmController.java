package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int uniqueId = 1;
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(uniqueId);
        films.put(uniqueId, film);
        uniqueId++;
        log.info("Добавлен новый фильм");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId()) || film.getId() == null) {
            log.warn("Фильм с таким id не найден");
            throw new ValidationException("Фильм с таким id не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
