-- Movies: normalized title + year + genres
CREATE TABLE IF NOT EXISTS movies (
    id           BIGINT PRIMARY KEY,
    title        TEXT        NOT NULL,
    genres       TEXT[]
);

-- Ratings: separate PK (safer for duplicates)
CREATE TABLE IF NOT EXISTS ratings (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT      NOT NULL,
    movie_id  BIGINT      NOT NULL,
    rating    NUMERIC(2,1) NOT NULL,
    rated_at  TIMESTAMP   NOT NULL
);

-- Tags
CREATE TABLE IF NOT EXISTS tags (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT,
    movie_id  BIGINT,
    tag       TEXT,
    tagged_at TIMESTAMP
);
