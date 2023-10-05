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

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    List<Film> getUserFilms(User user);
    List<Genre> getAllGenres();
    List<RateMPA> getAllRatings();
    List<Genre> getGenreByFilmID(Integer id);
    void addGenresToFilm(Genre genre, Integer filmId);
    Genre getGenreById(Integer id);
    RateMPA getRateByFilmID(Integer id);
    void addRateToFilm(RateMPA rate, Integer filmId);
    RateMPA getRateById(Integer id);
    void deleteAllGenresByFilm(Integer filmId);

}
