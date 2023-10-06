# java-filmorate
Template repository for Filmorate project.
![Диаграмма](https://github.com/zhnastya/java-filmorate/assets/123512924/69916c7b-d413-4b16-a773-9550eda4618f)

Получение списка фильмов
SELECT *
FROM film;

Получение фильма по id
SELECT *
FROM film
WHERE film_id = ?;

Получение списка пользователей
SELECT *
FROM user;

Получение пользователя по id
SELECT *
FROM user
WHERE user_id = ?;

Получение жанра фильма:
SELECT k.name
FROM film AS g
JOIN film_genre AS t ON g.film_id = t.film_id
JOIN genre AS k ON t.genre_id=k.genre_id
WHERE g.film_id = ?;

Получение рейтинга фильма:
SELECT t.name,
r.name
FROM rateMPA AS r
JOIN films AS t ON r.rateMPA_id = t.rateMPA_id
WHERE t.film_id = ?;

Получение списка лайкнутых фильмов пользователя:
SELECT t.name
FROM films AS t
JOIN likes AS l ON t.film_id = l.film_id
JOIN users AS u ON l.user_id = u.user_id
WHERE u.user_id = ?;

Получение списка общих друзей
SELECT *
FROM friends AS t
JOIN users AS u ON t.user_id = u.user_id
WHERE t.user_id = ?
AND t.friend_id IN (SELECT friend_id
FROM friends
WHERE user_id = ?);

Получение списка друзей пользователя
SELECT f.friend_id
FROM users AS u
JOIN friends AS t ON u.user_id = f.user_id
WHERE f.user_id = ?;
