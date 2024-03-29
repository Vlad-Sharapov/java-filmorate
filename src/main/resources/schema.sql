CREATE TABLE IF NOT EXISTS mpa (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name varchar DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name varchar NOT NULL,
  description varchar DEFAULT NULL,
  release_date timestamp NOT NULL,
  duration INTEGER NOT NULL,
  mpa_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  user_id1 INTEGER NOT NULL,
 user_id2 INTEGER NOT NULL,
  status tinyint DEFAULT false
);

CREATE TABLE IF NOT EXISTS genre (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  film_id int NOT NULL,
  genre_id int NOT NULL
);

CREATE TABLE IF NOT EXISTS enjoy (
  id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  user_id INTEGER NOT NULL,
  film_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  email varchar NOT NULL,
  login varchar NOT NULL,
  name varchar DEFAULT NULL,
  birthday timestamp DEFAULT NULL
);

ALTER TABLE enjoy ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE enjoy ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE friends ADD FOREIGN KEY (user_id1) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friends ADD FOREIGN KEY (user_id2) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE films ADD FOREIGN KEY (mpa_id) REFERENCES mpa (id);

ALTER TABLE film_genres ADD FOREIGN KEY (genre_id) REFERENCES genre (id);

ALTER TABLE film_genres ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE ;
