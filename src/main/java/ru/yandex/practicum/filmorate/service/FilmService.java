package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    public Genre getGenreById(Integer id){
        return storage.getGenreById(id);
    }

    public List<Genre> getAllGenres(){
        return storage.getAllGenres();
    }

    public List<RateMPA> getAllRatings(){
        return storage.getAllRatings();
    }

    public RateMPA getMpaById(Integer id){
        return storage.getRateById(id);
    }

    private Film getStorageFilmId(Integer id) {
        return storage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }


    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }


    public Film updateFilm(Film film) {
        storage.updateFilm(film);
        return getStorageFilmId(film.getId());
    }


    public Film getById(Integer id) {
        return getStorageFilmId(id);
    }


    public List<Film> getFilms() {
        return storage.getFilms();
    }


    public void addLike(Integer userId, Integer filmId) {
        storage.addLike(userId, filmId);
    }


    public void removeLike(Integer filmId, Integer userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id - " + userId + " не найден"));
        Film film = getStorageFilmId(filmId);
        List<Film> films1 = storage.getUserFilms(user);
        if (films1.isEmpty() || !films1.contains(film)) return;
        storage.removeLike(userId, filmId);
    }


    public List<Film> getPopular(Integer limit) {
        return storage.getFilms().stream()
                .sorted((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
