package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final Map<Integer, Set<Integer>> films = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public Film addLike(int id, int userId) {
        Set<Integer> listFilm;
        Film film = storage.getFilmById(id);
        if (films.get(userId) == null) listFilm = new HashSet<>();
        else listFilm = films.get(userId);
        if (listFilm.contains(id)) {
            return film;
        }
        film.setLikes(film.getLikes() + 1);
        storage.updateFilm(film);
        listFilm.add(id);
        films.put(userId, listFilm);
        log.info("Лайк успешно добавлен");
        return film;
    }

    public Film removeLike(int id, int userId) {
        Film film = storage.getFilmById(id);
        Set<Integer> listFilm;
        if (films.get(userId) == null) listFilm = new HashSet<>();
        else listFilm = films.get(userId);
        if (!listFilm.contains(id)) {
            return film;
        }
        film.setLikes(film.getLikes() - 1);
        storage.updateFilm(film);
        listFilm.remove(id);
        films.put(userId, listFilm);
        log.info("Лайк успешно удален");
        return film;
    }

    public Set<Film> getLikes(int limit) {
        log.info("Список самых популярных фильмов отправлен");
        return storage.getFilms().stream()
                .sorted((s, s1) -> -1 * Integer.compare(s.getLikes(), s1.getLikes()))
                .limit(limit)
                .collect(Collectors.toSet());
    }
}
