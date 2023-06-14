DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS request CASCADE;
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS booking CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40),
    email VARCHAR(40) UNIQUE
);

CREATE TABLE IF NOT EXISTS request
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(50),
    user_id INT REFERENCES users (id) ON DELETE CASCADE,
    created DATE
    );

CREATE TABLE IF NOT EXISTS item
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40),
    description VARCHAR(255),
    available BOOLEAN,
    owner_id INT REFERENCES users (id) ON DELETE CASCADE,
    request_id INT REFERENCES request (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(30),
    item_id INT REFERENCES item (id) ON DELETE CASCADE,
    booker_id INT REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comment
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(200),
    created TIMESTAMP WITHOUT TIME ZONE,
    item_id INT REFERENCES item (id) ON DELETE CASCADE,
    user_id INT REFERENCES users (id) ON DELETE CASCADE
);