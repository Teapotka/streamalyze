-- V4__populate_from_raw.sql

-- Make it idempotent on first run
TRUNCATE TABLE ratings RESTART IDENTITY CASCADE;
TRUNCATE TABLE tags RESTART IDENTITY CASCADE;
TRUNCATE TABLE movies RESTART IDENTITY CASCADE;

-- ========== MOVIES ==========
INSERT INTO movies (id, title, genres)
SELECT
    mr.movieid                                              AS id,
    -- strip " (1995)" from the end of the title, if present
    regexp_replace(mr.title, ' \(\d{4}\)$', '')             AS title,
    string_to_array(mr.genres, '|')::text[]                 AS genres
FROM movies_raw mr;

-- ========== RATINGS ==========
INSERT INTO ratings (user_id, movie_id, rating, rated_at)
SELECT
    rr.userid,
    rr.movieid,
    rr.rating,
    rr."timestamp"::timestamp                               AS rated_at
FROM ratings_raw rr;

-- ========== TAGS ==========
INSERT INTO tags (user_id, movie_id, tag, tagged_at)
SELECT
    tr.userid,
    tr.movieid,
    tr.tag,
    tr.tagged_at
FROM tags_raw tr;
