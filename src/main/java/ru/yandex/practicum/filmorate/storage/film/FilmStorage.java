package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film createFilm(Film film);

    void updateFilm(Film film);

    List<Film> getFilms();

    Optional<Film> getFilmById(int id);

    Film addLike(int userId, int filmId);

    Film removeLike(int filmId, int userId);

    Set<Film> getPopular();
}
