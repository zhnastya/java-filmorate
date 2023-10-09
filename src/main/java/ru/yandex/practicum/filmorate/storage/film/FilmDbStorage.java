package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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


    private void updateGenresByFilm(Film film, List<Genre> genre) {
        for (Genre genre1 : genre) {
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
            params.put("film_id", film.getId());
            params.put("genre_id", genre1.getId());
            namedParameterJdbcTemplate.update(sql, params);
        }
    }


    private List<Film> getAllFilms(String sql) {
        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<Film> list = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setLikes(rs.getInt("likes"));
                film.setMpa(new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                list.add(film);
            }
            return list;
        });
    }

    private List<Genre> loadFilmGenres(List<Film> films) {
        List<Genre> genres = new ArrayList<>();
        for (Film film : films) {
            String sql = "SELECT g.genre_id, " +
                    "g.genre_name " +
                    "FROM film_genre AS f " +
                    "JOIN genres AS g ON f.genre_id = g.genre_id " +
                    "WHERE f.film_id = :film_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("film_id", film.getId());

            genres = namedParameterJdbcTemplate.query(sql, mapper, (rs, rowNum) -> new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            ));
            film.setGenres(genres);
        }
        return genres;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            updateGenresByFilm(film, film.getGenres());
        }
    }


    @Override
    public List<Genre> getGenreByFilm(Film film) {
        return loadFilmGenres(List.of(film));
    }


    @Override
    public void deleteAllGenresByFilm(Film film) {
        String sql = "DELETE " +
                "FROM film_genre " +
                "WHERE film_id = :film_id ";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", film.getId());
        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public Film createFilm(Film film) {
        String sql = "insert into films " +
                "(name, description, release_date, likes, duration, mpa_id) " +
                "values(:name, :description, :release_date, :likes, :duration, :mpa_id)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("likes", film.getLikes())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);
        film.setId((Integer) keyHolder.getKey());
        saveGenres(film);
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
            deleteAllGenresByFilm(film);
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                updateGenresByFilm(film, film.getGenres());
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неверный id пользователя или фильма");
        }
    }


    @Override
    public List<Film> getFilms() {
        String sql = "SELECT *" +
                "FROM films AS f " +
                "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id";
        List<Film> films = getAllFilms(sql);
        loadFilmGenres(films);

        return films;
    }


    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            String sql = "SELECT * " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE f.film_id = :film_id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("film_id", id);

            Optional<Film> film = namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> Optional.of(new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("likes"),
                            new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                            new ArrayList<>()
                    )));
            assert Objects.requireNonNull(film).orElse(null) != null;
            loadFilmGenres(List.of(film.orElse(null)));
            return film;

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
    public void removeLike(User user, Film film) {
        try {
            String sql = "DELETE " +
                    "FROM likes " +
                    "WHERE film_id = :film_id " +
                    "AND user_id = :user_id;" +
                    "UPDATE films " +
                    "SET likes = likes - 1 " +
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
    public List<Film> getUserFilms(User user) {
        try {
            String sql = "SELECT * " +
                    "FROM films AS t " +
                    "LEFT JOIN mpa_ratings AS mr ON t.mpa_id = mr.mpa_id " +
                    "JOIN likes AS l ON t.film_id = l.film_id " +
                    "JOIN users AS u ON l.user_id = u.user_id " +
                    "WHERE u.user_id = :user_id";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());

            List<Film> films = new ArrayList<>(namedParameterJdbcTemplate.query(sql, params,
                    (rs, rowNum) -> new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("likes"),
                            new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                            new ArrayList<>())));
            loadFilmGenres(films);
            return films;

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", user.getId()));
        }
    }
}
