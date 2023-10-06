package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenresAndMpaController {
    private final FilmService service;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Запрос на получение списка всех жанров");
        List<Genre> genres = service.getAllGenres();
        log.info("Список жанров отправлен");
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable("id") Integer id) {
        log.info("Запрос на получение жанра по id");
        Genre genre = service.getGenreById(id);
        log.info("Жанр отправлен");
        return genre;
    }

    @GetMapping("/mpa")
    public List<RateMPA> getAllMpa() {
        log.info("Запрос на получение списка всех рейтингов");
        List<RateMPA> rate = service.getAllRatings();
        log.info("Список рейтингов отправлен");
        return rate;
    }

    @GetMapping("/mpa/{id}")
    public RateMPA getMpaById(@PathVariable("id") Integer id) {
        log.info("Запрос на получение рейтинга по id");
        RateMPA rate = service.getMpaById(id);
        log.info("Рейтинг отправлен");
        return rate;
    }
}
