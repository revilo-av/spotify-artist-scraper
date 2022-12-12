CREATE TABLE album (
   id UUID NOT NULL,
   name VARCHAR(255) NOT NULL,
   artists_id UUID,
   create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   album_group VARCHAR(255),
   album_type VARCHAR(255),
   release_date VARCHAR(255),
   type VARCHAR(255),
   spotify_id VARCHAR(255),
   changed BOOLEAN DEFAULT FALSE NOT NULL,
   CONSTRAINT pk_album PRIMARY KEY (id)
);


CREATE TABLE artists (
   id UUID NOT NULL,
   name VARCHAR(255),
   create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   spotify_id VARCHAR(255),
   changed BOOLEAN DEFAULT FALSE NOT NULL,
   CONSTRAINT pk_artists PRIMARY KEY (id)
);

ALTER TABLE album ADD CONSTRAINT FK_ALBUM_ON_ARTISTS FOREIGN KEY (artists_id) REFERENCES artists (id);

ALTER TABLE artists ADD CONSTRAINT uc_artists_name UNIQUE (name);

ALTER TABLE artists ADD CONSTRAINT uc_artists_spotifyid UNIQUE (spotify_id);