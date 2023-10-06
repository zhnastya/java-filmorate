package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Qualifier("FilmDbStorage")
@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private Map<String, Object> getParametrs(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("likes", film.getLikes());
        parameters.put("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null);
        parameters.put("film_id", film.getId());
        return parameters;
    }


    public void cleanAllFilm() {
        String sql = "DELETE FROM films";
        jdbcTemplate.execute(sql);
    }


    @Override
    public List<Genre> getGenreByFilmID(Integer id) {
        String sql = "SELECT g.genre_id, " +
                "g.name " +
                "FROM film_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id " +
                "WHERE f.film_id = :film_id";

        Map<String, Object> mapper = new HashMap<>();
        mapper.put("film_id", id);

        return namedParameterJdbcTemplate.query(sql, mapper, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ));
    }


    @Override
    public RateMPA getRateByFilmID(Integer id) {
        try {
            String sql = "SELECT mr.mpa_id, " +
                    "mr.name " +
                    "FROM mpa_ratings AS mr " +
                    "JOIN films AS f ON mr.mpa_id = f.mpa_id " +
                    "WHERE f.film_id = :film_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("film_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new RateMPA(rs.getInt("mpa_id"),
                            rs.getString("name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id %d не найден.", id));
        }
    }


    @Override
    public void addGenresToFilm(Genre genre, Integer filmId) {
        String sql = "MERGE " +
                "INTO film_genre AS f " +
                "USING VALUES (:film_id, :genre_id) AS val(film_id, genre_id) " +
                "ON f.film_id = val.film_id " +
                "AND f.genre_id = val.genre_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT " +
                "VALUES (val.film_id, val.genre_id) " +
                "WHEN MATCHED THEN " +
                "UPDATE " +
                "SET f.film_id= :film_id, " +
                "f.genre_id = :genre_id";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        params.put("genre_id", genre.getId());
        int rows = namedParameterJdbcTemplate.update(sql, params);
        if (rows == 0) {
            log.warn("Фильм с id {} не найден.", filmId);
            throw new NotFoundException(
                    String.format("Фильм с id %d не найден.", filmId));
        }
    }


    @Override
    public Genre getGenreById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM genres  " +
                    "WHERE genre_id = :genre_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("genre_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                            rs.getString("name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id %d не найден.", id));
        }
    }


    @Override
    public void addRateToFilm(RateMPA rate, Integer filmId) {
        String sql = "UPDATE films " +
                "SET mpa_id = :mpa_id " +
                "WHERE film_id = :film_id";
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("film_id", filmId);
        mapper.put("mpa_id", rate.getId());

        int rows = namedParameterJdbcTemplate.update(sql, mapper);
        if (rows == 0) {
            log.warn("Фильм с id {} не найден.", filmId);
            throw new NotFoundException(
                    String.format("Фильм с id %d не найден.", filmId));
        }
    }


    @Override
    public RateMPA getRateById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM mpa_ratings  " +
                    "WHERE mpa_id = :mpa_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("mpa_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new RateMPA(rs.getInt("mpa_id"),
                            rs.getString("name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Рейтинг с id %d не найден.", id));
        }
    }


    @Override
    public void deleteAllGenresByFilm(Integer filmId) {
        String sql = "DELETE " +
                "FROM film_genre " +
                "WHERE film_id = :film_id ";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", filmId);
        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public Film createFilm(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(getParametrs(film)).intValue());

        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres().stream()
                    .map(Genre::getId)
                    .map(this::getGenreById)
                    .peek(genre -> addGenresToFilm(genre, film.getId()))
                    .collect(Collectors.toList());
            film.setGenres(genres);
        } else {
            film.setGenres(new ArrayList<>());
        }
        return film;
    }


    @Override
    public void updateFilm(Film film) {
        try {
            String sql = "UPDATE films " +
                    "SET name = :name, " +
                    "description = :description, " +
                    "release_date = :release_date, " +
                    "duration = :duration," +
                    "likes= :likes, " +
                    "mpa_id = :mpa_id " +
                    "WHERE film_id = :film_id";

            int rows = namedParameterJdbcTemplate.update(sql, getParametrs(film));
            if (rows == 0) {
                log.warn("Фильм с id {} не найден.", film.getId());
                throw new NotFoundException(
                        String.format("Фильм с id %d не найден.", film.getId()));
            }
            deleteAllGenresByFilm(film.getId());
            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    addGenresToFilm(genre, film.getId());
                }
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public List<Film> getFilms() {
        String sql = "SELECT *" +
                "FROM films";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("likes"),
                getRateByFilmID(rs.getInt("film_id")),
                getGenreByFilmID(rs.getInt("film_id"))
        ));
    }


    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            String sql = "SELECT * " +
                    "FROM films " +
                    "WHERE film_id = :film_id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("film_id", id);

            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> Optional.of(new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("likes"),
                            getRateByFilmID(rs.getInt("film_id")),
                            getGenreByFilmID(rs.getInt("film_id"))
                    )));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public void addLike(Integer userId, Integer filmId) {
        try {

            String sql = "MERGE " +
                    "INTO likes AS l " +
                    "USING VALUES (:film_id, :user_id) AS val(film_id, user_id) " +
                    "ON l.film_id = val.film_id " +
                    "AND l.user_id = val.user_id " +
                    "WHEN NOT MATCHED THEN " +
                    "INSERT " +
                    "VALUES (val.film_id, val.user_id);" +
                    "UPDATE films " +
                    "SET likes = likes + 1 " +
                    "WHERE film_id = :film_id";

            Map<String, Object> params = new HashMap<>();
            params.put("film_id", filmId);
            params.put("user_id", userId);
            namedParameterJdbcTemplate.update(sql, params);

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public void removeLike(Integer userId, Integer filmId) {
        try {
            String sql = "DELETE " +
                    "FROM likes " +
                    "WHERE film_id = :film_id " +
                    "AND user_id = :user_id;" +
                    "UPDATE films " +
                    "SET likes = likes - 1 " +
                    "WHERE film_id = :film_id";

            Map<String, Object> params = new HashMap<>();
            params.put("film_id", filmId);
            params.put("user_id", userId);

            namedParameterJdbcTemplate.update(sql, params);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public List<Film> getUserFilms(User user) {
        try {
            String sql = "SELECT * " +
                    "FROM films AS t " +
                    "JOIN likes AS l ON t.film_id = l.film_id " +
                    "JOIN users AS u ON l.user_id = u.user_id " +
                    "WHERE u.user_id = :user_id";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());

            return new ArrayList<>(namedParameterJdbcTemplate.query(sql, params,
                    (rs, rowNum) -> new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("likes"),
                            getRateByFilmID(rs.getInt("film_id")),
                            getGenreByFilmID(rs.getInt("film_id")))));

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", user.getId()));
        }
    }


    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * " +
                "FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                rs.getString("name")));
    }

    @Override
    public List<RateMPA> getAllRatings() {
        String sql = "SELECT * " +
                "FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new RateMPA(rs.getInt("mpa_id"),
                rs.getString("name")));
    }
}
