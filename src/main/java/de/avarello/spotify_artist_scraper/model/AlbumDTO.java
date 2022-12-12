package de.avarello.spotify_artist_scraper.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.UUID;

@JsonRootName(value = "album")
public record AlbumDTO(
        String name,
        UUID artistId,
        String albumGroup,
        String albumType,
        String releaseDate,
        String type,
        String spotifyId) {
}
