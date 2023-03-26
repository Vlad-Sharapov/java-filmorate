# java-filmorate
Template repository for Filmorate project.

## 1. База данных приложения
Схема базы данных приведена на рисунке 1.1

![filmorate_db](/Users/vsh/Documents/Java/java-filmorate/src/main/resources/filmorate_db.png)
Рис. 1.1 - Схема базы данных

### 1.1 Пример запросов к БД

```sql
-- Проверка наличия фильма в БД
SELECT *
FROM films
WHERE name = {name}

-- Фильм с id = {filmId}
SELECT f.name,
f.description,
f.release_date,
f.duration,
g.name,
c.name
FROM films f
JOIN genre g ON f.genre_id = g.id
JOIN category c ON f.category_id = c.id
WHERE f.id = {filmId}

-- Все фильмы
SELECT f.name,
f.description,
f.release_date,
f.duration,
g.name,
c.name
FROM films f
JOIN genre g ON f.genre_id = g.id
JOIN category c ON f.category_id = c.id

-- Количество лайков у фильма с id = {filmId}
SELECT COUNT(user_id)
FROM enjoy l
WHERE film_id = {filmId}
GROUP BY film_id

-- Топ {N} популярных фильмов
SELECT f.id, f.name, COUNT(e.user_id) number_of_likes
FROM films f
JOIN enjoy e ON f.id = e.film_id
GROUP By f.id
ORDER BY number_of_likes DESC
LIMIT {N}

-- Получение пользователя по его {userId}
SELECT *
FROM users
WHERE id = {userId}

-- Получение друзей пользователя {userId}
SELECT user_id2
FROM friends
WHERE user_id1 = {userId}

-- Общие друзья
SELECT f1.user_id2
FROM friends f1
WHERE f1.user_id1 = {userId1}
AND status = 1
AND f1.user_id2 = (SELECT f2.user_id2
FROM friends f2
WHERE f2.user_id1 = {userId2}
AND status = 1)
```


