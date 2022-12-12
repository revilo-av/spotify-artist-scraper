package de.avarello.spotify_artist_scraper.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;
import java.util.UUID;

@JsonRootName(value = "artists")
public record ReadArtistsDTO(
        UUID id,
        String name,
        String spotifyId,
        Date createDate,
        Date updateDate
) {


}
