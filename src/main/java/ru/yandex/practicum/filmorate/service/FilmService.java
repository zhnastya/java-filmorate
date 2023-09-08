package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }


    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }


    public Film updateFilm(Film film) {
        storage.updateFilm(film);
        return film;
    }


    public Film getById(int id) {
        return storage.getFilmById(id).orElseThrow();
    }


    public List<Film> getFilms() {
        return storage.getFilms();
    }


    public Film addLike(int userId, int filmId) {
        return storage.addLike(userId, filmId);
    }


    public Film removeLike(int filmId, int userId) {
        return storage.removeLike(filmId, userId);
    }


    public Set<Film> getPopular(int limit) {
        return storage.getPopular()
                .stream()
                .limit(limit)
                .collect(Collectors.toSet());
    }
}
