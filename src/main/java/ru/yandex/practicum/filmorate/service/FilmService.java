package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.ValidatorService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final List<Film> popular = new ArrayList<>();

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }


    public Film createFilm(Film film) {
        Film film1 = storage.createFilm(film);
        popular.add(film1);
        return film1;
    }


    public Film updateFilm(Film film) {
        Film film1 = getById(film.getId());
        popular.remove(film1);
        storage.updateFilm(film);
        popular.add(getById(film.getId()));
        return film;
    }


    public Film getById(int id) {
        ValidatorService.validateId(id);
        return storage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }


    public List<Film> getFilms() {
        return storage.getFilms();
    }


    public Film addLike(int userId, int filmId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Film film = getById(filmId);
        List<Film> films1 = storage.getUserFilms(user);
        if (films1.contains(film)) {
            return film;
        }
        storage.addLike(user, film);
        updateFilm(film);
        return getById(filmId);
    }


    public Film removeLike(int filmId, int userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Film film = getById(filmId);
        List<Film> films1 = storage.getUserFilms(user);
        if (films1.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не лайкнул ни одного фильма");
        }
        if (!films1.contains(film)) {
            throw new NotFoundException("Пользователь " + userId + " не лайкнул фильм с id - " + filmId);
        }
        storage.removeLike(user, film);
        updateFilm(film);
        return getById(filmId);
    }


    public List<Film> getPopular(int limit) {
        return popular.stream()
                .sorted((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
