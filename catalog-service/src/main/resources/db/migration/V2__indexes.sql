-- Movies
CREATE INDEX IF NOT EXISTS idx_movies_title
    ON movies (title);

CREATE INDEX IF NOT EXISTS idx_movies_year
    ON movies (release_year);

CREATE INDEX IF NOT EXISTS idx_movies_genres_gin
    ON movies USING GIN (genres);

-- Ratings
CREATE INDEX IF NOT EXISTS idx_ratings_movie
    ON ratings (movie_id);

CREATE INDEX IF NOT EXISTS idx_ratings_user
    ON ratings (user_id);

-- Tags
CREATE INDEX IF NOT EXISTS idx_tags_movie
    ON tags (movie_id);

CREATE INDEX IF NOT EXISTS idx_tags_user
    ON tags (user_id);
