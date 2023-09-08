package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int uniqueId = 1;
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private final Map<User, Set<Film>> userFilms = new HashMap<>();
    private final Set<Film> popular = new TreeSet<>((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()));
    private final UserStorage storage;

    @Autowired
    public InMemoryFilmStorage(UserStorage storage) {
        this.storage = storage;
    }


    @Override
    public Film addLike(int userId, int filmId) {
        User user = storage.getUserById(userId).orElseThrow();
        Film film = getFilmById(filmId).orElseThrow();
        if (!userFilms.containsKey(user)) {
            film.setLikes(film.getLikes() + 1);
            userFilms.put(user, Set.of(film));
            updateFilm(film);
            return film;
        }
        Set<Film> films1 = userFilms.get(user);
        if (!films1.contains(film)) {
            film.setLikes(film.getLikes() + 1);
            films1.add(film);
            userFilms.put(user, films1);
            updateFilm(film);
            return film;
        }
        return films1.stream()
                .filter(s -> s.equals(film))
                .findFirst()
                .map(s -> {
                    s.setLikes(s.getLikes() + 1);
                    updateFilm(s);
                    return s;
                })
                .orElse(null);
    }


    @Override
    public Film removeLike(int filmId, int userId) {
        User user = storage.getUserById(userId).orElseThrow();
        Film film = getFilmById(filmId).orElseThrow();
        if (!userFilms.containsKey(user)) {
            throw new NotFoundException("Пользователь " + userId + " не лайкнул ни одного фильма");
        }
        Set<Film> films1 = userFilms.get(user);
        if (!films1.contains(film)) {
            throw new NotFoundException("Пользователь " + userId + " не лайкнул фильм с id - " + filmId);
        }
        return films1.stream()
                .filter(s -> s.equals(film))
                .findFirst()
                .map(s -> {
                    s.setLikes(s.getLikes() - 1);
                    updateFilm(s);
                    return s;
                })
                .orElse(null);
    }


    @Override
    public Set<Film> getPopular() {
        return popular;
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
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        popular.add(film);
        films.put(film.getId(), film);
    }


    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }


    @Override
    public Optional<Film> getFilmById(int id) {
        Optional<Film> film = films.values().stream()
                .filter(film1 -> film1.getId() == id)
                .findFirst();
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }
}
