\c coin

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS portfolio_coin;
DROP TABLE IF EXISTS portfolio;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS coin;


CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    email      VARCHAR(255) NOT NULL,
    nick_name  VARCHAR(255) NOT NULL,
    password   VARCHAR(255),
    user_role  VARCHAR(255) NOT NULL CHECK (user_role IN ('USER', 'ADMIN'))
);


CREATE TABLE portfolio
(
    portfolio_id SERIAL PRIMARY KEY,
    name         varchar(255) NOT NULL,
    user_id      UUID         NOT NULL,
    created_at   TIMESTAMP(6),
    updated_at   TIMESTAMP(6),
    deleted_at   TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE coin
(
    id SERIAL PRIMARY KEY,
    symbol varchar(50) NOT NULL,
    name varchar(255) NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6)
);


CREATE TABLE portfolio_coin
(
    id           SERIAL PRIMARY KEY,
    portfolio_id BIGINT       NOT NULL,
    coin_id BIGINT NOT NULL,
    created_at   TIMESTAMP(6),
    updated_at   TIMESTAMP(6),
    deleted_at   TIMESTAMP(6),
    FOREIGN KEY (portfolio_id) REFERENCES portfolio (portfolio_id) ON DELETE CASCADE,
    FOREIGN KEY (coin_id) REFERENCES coin (id) ON DELETE CASCADE
);


