package de.avarello.spotify_artist_scraper.repos;

import de.avarello.spotify_artist_scraper.domain.Artist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistsRepository extends JpaRepository<Artist, UUID> {
    @Query("select (count(a) > 0) from Artist a where a.name = ?1")
    boolean existsByName(String name);

    Artist findBySpotifyId(String spotifyId);

    @EntityGraph(attributePaths = {"albums"})
    @Query("select (count(a) > 0) from Artist a where a.spotifyId = ?1")
    boolean existArtistWithSpotifyId(String spotifyId);

}
