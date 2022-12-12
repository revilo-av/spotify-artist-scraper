package de.avarello.spotify_artist_scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;
import java.util.UUID;

@JsonRootName(value = "album")
public record ReadAlbumDTO(
        UUID id,
        String name,
        @JsonProperty("artists")
        ReadArtistsDTO readArtistsDTO,
        String albumGroup,
        String albumType,
        String releaseDate,
        String type,
        String spotifyId,
        Date lastUpdated,
        Date dateCreated
) {
}
