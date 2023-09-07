package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping
public class FilmController {
    private final FilmStorage storage;
    private final FilmService service;

    @Autowired
    public FilmController(FilmStorage storage, FilmService service) {
        this.storage = storage;
        this.service = service;
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return storage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return storage.updateFilm(film);
    }

    @GetMapping("/films")
    public Set<Film> getFilms() {
        return storage.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable int id) {
        if (id < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return storage.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        if (id < 0 || userId < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.addLike(userId, id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        if (id < 0 || userId < 0) {
            throw new NotFoundException("Значения id не могут быть отрицательными");
        }
        return service.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Set<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return service.getPopular(count);
    }
}
