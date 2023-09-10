package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int uniqueId = 1;
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private final Map<User, List<Film>> userFilms = new HashMap<>();


    @Override
    public void addLike(User user, Film film) {
        List<Film> filmSet = getUserFilms(user);
        film.setLikes(film.getLikes() + 1);
        filmSet.add(film);
        userFilms.put(user, filmSet);
    }


    @Override
    public void removeLike(User user, Film film) {
        List<Film> films1 = getUserFilms(user);
        films1.remove(film);
        film.setLikes(film.getLikes() - 1);
        userFilms.put(user, films1);
    }


    @Override
    public List<Film> getUserFilms(User user) {
        return userFilms.containsKey(user) ? userFilms.get(user) : new ArrayList<>();
    }


    @Override
    public Film createFilm(Film film) {
        film.setId(uniqueId);
        film.setLikes(0);
        films.put(uniqueId, film);
        uniqueId++;
        return film;
    }


    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }


    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }


    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }
}
