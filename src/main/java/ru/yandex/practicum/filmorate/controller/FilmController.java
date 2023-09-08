package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        Film film1 = service.createFilm(film);
        log.info("Добавлен новый фильм");
        return film1;
    }


    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма");
        Film film1 = service.updateFilm(film);
        log.info("Фильм обновлен");
        return film1;
    }


    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос на получение списка всех фильмов");
        List<Film> films = service.getFilms();
        log.info("Список всех фильмов отправлен");
        return films;
    }


    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Запрос на получение фильма с id - " + id);
        Film film = service.getById(id);
        log.info("Фильм с id - " + id + " отправлен");
        return film;
    }


    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на добавление лайка фильму - " + id);
        Film film = service.addLike(userId, id);
        log.info("Пользователь с id - " + userId + " поставил лайк фильму - " + id);
        return film;
    }


    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка фильму - " + id);
        Film film = service.removeLike(id, userId);
        log.info("Пользователь с id - " + userId + " удалил лайк фильму - " + id);
        return film;
    }


    @GetMapping("/popular")
    public Set<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение списка популярных фильмов");
        Set<Film> films = service.getPopular(count);
        log.info("Список популярных фильмов отправлен");
        return films;
    }
}
