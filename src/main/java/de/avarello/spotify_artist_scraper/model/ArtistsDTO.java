package de.avarello.spotify_artist_scraper.model;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "artists")
public record ArtistsDTO(
        String name
) {
}
