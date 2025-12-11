CREATE TABLE IF NOT EXISTS ratings_raw (
      userid      BIGINT,
      movieid     BIGINT,
      rating      NUMERIC(2,1),
      "timestamp" TIMESTAMP
    );
CREATE TABLE IF NOT EXISTS tags_raw (
      userid    BIGINT,
      movieid   BIGINT,
      tag       TEXT,
      tagged_at TIMESTAMP
    );
CREATE TABLE IF NOT EXISTS movies_raw (
      movieid BIGINT,
      title   TEXT,
      genres  TEXT
    );

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM movies_raw LIMIT 1) THEN
        RAISE NOTICE 'Importing movies_raw from /data/movie.csv ...';
        EXECUTE $COPY$
            COPY movies_raw(movieid, title, genres)
            FROM '/data/movie.csv'
            DELIMITER ',' CSV HEADER
        $COPY$;
    ELSE
        RAISE NOTICE 'Skipping movies_raw import – table already has data.';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ratings_raw LIMIT 1) THEN
        RAISE NOTICE 'Importing ratings_raw from /data/rating.csv ...';
        EXECUTE $COPY$
            COPY ratings_raw(userid, movieid, rating, "timestamp")
            FROM '/data/rating.csv'
            DELIMITER ',' CSV HEADER
        $COPY$;
    ELSE
        RAISE NOTICE 'Skipping ratings_raw import – table already has data.';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM tags_raw LIMIT 1) THEN
        RAISE NOTICE 'Importing tags_raw from /data/tag.csv ...';
        EXECUTE $COPY$
            COPY tags_raw(userid, movieid, tag, tagged_at)
            FROM '/data/tag.csv'
            DELIMITER ',' CSV HEADER
        $COPY$;
    ELSE
        RAISE NOTICE 'Skipping tags_raw import – table already has data.';
    END IF;
END
$$;
-- COPY movies_raw(movieid, title, genres) FROM '/data/movie.csv' DELIMITER ',' CSV HEADER;
-- COPY ratings_raw(userid, movieid, rating, "timestamp") FROM '/data/rating.csv' DELIMITER ',' CSV HEADER;
-- COPY tags_raw(userid, movieid, tag, tagged_at) FROM '/data/tag.csv' DELIMITER ',' CSV HEADER;
