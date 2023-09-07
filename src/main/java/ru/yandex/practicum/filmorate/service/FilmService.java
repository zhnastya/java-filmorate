package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
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

    public Film addLike(int userId, int filmId) {
        Set<Integer> filmsId;
        Film film = storage.getFilmById(filmId);
        if (films.isEmpty() || !films.containsKey(userId)) {
            filmsId = new HashSet<>();
        } else {
            filmsId = films.get(userId);
        }
        if (filmsId.add(filmId)) {
            film.setLikes(film.getLikes() + 1);
            films.put(userId, filmsId);
            log.info("Лайк успешно добавлен");
        }
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Set<Integer> filmsId;
        Film film = storage.getFilmById(filmId);
        if (films.isEmpty() || !films.containsKey(userId)) {
            throw new NotFoundException("У фильма нет лайков");
        } else {
            filmsId = films.get(userId);
        }
        if (filmsId.remove(filmId)) {
            film.setLikes(film.getLikes() - 1);
            films.put(userId, filmsId);
            log.info("Лайк успешно удален");
        }
        return film;
    }

    public Set<Film> getPopular(int limit) {
        log.info("Список самых популярных фильмов отправлен");
        return storage.getFilms().stream()
                .sorted((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()))
                .limit(limit)
                .collect(Collectors.toSet());
    }
}
