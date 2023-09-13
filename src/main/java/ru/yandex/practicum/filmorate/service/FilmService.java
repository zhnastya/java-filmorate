package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    private Film getStorageFilmId(Integer id) { //создала приватный метод, чтобы избежать дублирования кода
        return storage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }


    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }


    public Film updateFilm(Film film) {
        getStorageFilmId(film.getId()); // вызвала метод, чтобы проверить не выброситься ли исключение
        storage.updateFilm(film);
        return film;
    }


    public Film getById(Integer id) {
        return getStorageFilmId(id);
    }


    public List<Film> getFilms() {
        return storage.getFilms();
    }


    public void addLike(Integer userId, Integer filmId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Film film = getStorageFilmId(filmId);
        storage.addLike(user, film);
    }


    public void removeLike(Integer filmId, Integer userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id - " + userId + " не найден"));
        Film film = getStorageFilmId(filmId);
        List<Film> films1 = storage.getUserFilms(user);
        if (films1.isEmpty() || !films1.contains(film)) return;
        storage.removeLike(user, film);
    }


    public List<Film> getPopular(Integer limit) {
        return storage.getFilms().stream()
                .sorted((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
