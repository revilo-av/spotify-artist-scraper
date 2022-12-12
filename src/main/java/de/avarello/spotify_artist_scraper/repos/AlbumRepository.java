package de.avarello.spotify_artist_scraper.repos;

import de.avarello.spotify_artist_scraper.domain.Album;
import de.avarello.spotify_artist_scraper.domain.Artist;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Query("select (count(a) > 0) from Album a where a.spotifyId = ?1")
    boolean existWithSpotifyId(String spotifyId);

    @EntityGraph(attributePaths = {"artist"})
    @Query("select a from Album a where a.spotifyId = ?1")
    Album findBySpotifyId(String spotifyId);

    @EntityGraph(attributePaths = {"artist.albums"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select distinct a from Album a where a.artist = ?1")
    List<Album> findDistinctByArtist(Artist artist);


    @EntityGraph(attributePaths = {"artist"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select a from Album a")
    @Override
    List<Album> findAll(Sort sort);

    @Query("select (count(a) > 0) from Album a where a.spotifyId = ?1 and a.changed = true")
    boolean existsBySpotifyIdAndChangedTrue(String spotifyId);


    @EntityGraph(attributePaths = {"artist"})
    @Override
    Optional<Album> findById(UUID uuid);
}
