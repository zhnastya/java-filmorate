package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private Map<String, Object> getParams(Film film) {
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

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, " +
                "g.name " +
                "FROM film_genre AS fg " +
                "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = :film_id";
        Map<String, Object> map = new HashMap<>();
        map.put("film_id", filmId);

        return new ArrayList<>(namedParameterJdbcTemplate.query(sql, map, (rs1, rowNum) -> new Genre(
                rs1.getInt("genre_id"),
                rs1.getString("name"))));
    }


    private void updateGenresByFilm(Integer filmId, Integer genreId) {
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
        params.put("genre_id", genreId);
        namedParameterJdbcTemplate.update(sql, params);
    }


    private List<Film> getAllFilms(String sql) {
        return namedParameterJdbcTemplate.query(sql,
                rs -> {
                    List<Film> list = new ArrayList<>();
                    while (rs.next()) {
                        Film film = new Film();
                        film.setId(rs.getInt("film_id"));
                        film.setName(rs.getString("name"));
                        film.setDescription(rs.getString("description"));
                        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        film.setDuration(rs.getInt("duration"));
                        film.setLikes(rs.getInt("likes"));
                        film.setMpa(getRateByFilmId(film.getId()));
                        film.setGenres(getFilmGenres(film.getId()));
                        list.add(film);
                    }
                    return list;
                });
    }


    @Override
    public List<Genre> getGenreByFilmId(Integer id) {
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
    public RateMPA getRateByFilmId(Integer id) {
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
        updateGenresByFilm(filmId, genre.getId());
    }


    @Override
    public void addRateToFilm(RateMPA rate, Integer filmId) {
        String sql = "UPDATE films " +
                "SET mpa_id = :mpa_id " +
                "WHERE film_id = :film_id";
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("film_id", filmId);
        mapper.put("mpa_id", rate.getId());

        namedParameterJdbcTemplate.update(sql, mapper);
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

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(getParams(film)).intValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                addGenresToFilm(genre, film.getId());
            }
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

            namedParameterJdbcTemplate.update(sql, getParams(film));
            deleteAllGenresByFilm(film.getId());
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                for (Genre genre : film.getGenres()) {
                    updateGenresByFilm(film.getId(), genre.getId());
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

        return getAllFilms(sql);
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
                            getRateByFilmId(rs.getInt("film_id")),
                            getGenreByFilmId(rs.getInt("film_id"))
                    )));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public void addLike(User user, Film film) {
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
            params.put("film_id", film.getId());
            params.put("user_id", user.getId());
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
                            getRateByFilmId(rs.getInt("film_id")),
                            getGenreByFilmId(rs.getInt("film_id")))));

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", user.getId()));
        }
    }
}
