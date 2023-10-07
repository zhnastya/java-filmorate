package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    void updateFilm(Film film);

    List<Film> getFilms();

    Optional<Film> getFilmById(int id);

    void addLike(User user, Film film);

    void removeLike(Integer userId, Integer filmId);

    List<Film> getUserFilms(User user);

    void addGenresToFilm(Genre genre, Integer filmId);

    List<Genre> getGenreByFilmId(Integer id);

    RateMPA getRateByFilmId(Integer id);

    void addRateToFilm(RateMPA rate, Integer filmId);

    void deleteAllGenresByFilm(Integer filmId);
}
