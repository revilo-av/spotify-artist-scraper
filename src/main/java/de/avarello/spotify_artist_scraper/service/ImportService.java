package de.avarello.spotify_artist_scraper.service;

import de.avarello.spotify_artist_scraper.model.ArtistProperty;
import de.avarello.spotify_artist_scraper.model.FileResourceData;
import de.avarello.spotify_artist_scraper.Mapper;
import de.avarello.spotify_artist_scraper.domain.Album;
import de.avarello.spotify_artist_scraper.domain.Artist;
import de.avarello.spotify_artist_scraper.repos.AlbumRepository;
import de.avarello.spotify_artist_scraper.repos.ArtistsRepository;
import de.avarello.spotify_artist_scraper.repos.SpotifyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ImportService {
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    private final AlbumRepository albumRepository;
    private final ArtistsRepository artistsRepository;
    private final SpotifyRepository spotifyRepository;

    @Autowired
    public ImportService(final AlbumRepository albumRepository, final ArtistsRepository artistsRepository, SpotifyRepository spotifyRepository) {
        this.albumRepository = albumRepository;
        this.artistsRepository = artistsRepository;
        this.spotifyRepository = spotifyRepository;
    }

    public UUID importFromSpotify(String id) {

        List<AlbumSimplified> albumSimplified = spotifyRepository.getArtistById(id);

        var artists = getAlbumArtists(albumSimplified);

        artists = saveArtists(artists);

        List<Album> albums = getAlbumWithArtists(albumSimplified, artists);

        saveAlbums(albums);

        return artists.stream().toList().get(0).getId();
    }

    private Set<Artist> saveArtists(Set<Artist> artists) {
        return artists.stream()
                .map(artist -> {
                    try {
                        return saveOrReset(artist);
                    } catch (Exception exception) {
                        logger.error("Error By Store Artist Data {0}", exception);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<Album> saveAlbums(List<Album> albums) {
        return albums.stream()
                .map(album -> {
                    try {
                        return saveOrReset(album);
                    } catch (Exception ex) {
                        logger.error("Error By Store Album Data {0}", ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void importArtistPropertyFile() {
        List<ArtistProperty> fetchArtists = FileResourceData.getArtistList();

        fetchArtists.forEach(date -> importFromSpotify(date.getId()));
    }

    private List<Album> getAlbumWithArtists(List<AlbumSimplified> albumSimplified, Set<Artist> artists) {

        return albumSimplified.stream()
                .map(album -> {

                    final int firstArtistPosition = 0;

                    var firstArtist = album.getArtists()[firstArtistPosition];

                    var currentArtist = getArtistById(artists, firstArtist);

                    var mapedAlbum = Mapper.mapToAlbum(album);

                    currentArtist.ifPresent(mapedAlbum::setArtist);
                    return mapedAlbum;
                }).collect(Collectors.toList());
    }

    private static Optional<Artist> getArtistById(Set<Artist> artists, ArtistSimplified firstArtist) {
        return artists.stream()
                .filter(artist -> artist.getSpotifyId().equals(firstArtist.getId()))
                .findFirst();

    }

    private static Set<Artist> getAlbumArtists(List<AlbumSimplified> albumSimplified) {
        return albumSimplified.stream()
                .map(AlbumSimplified::getArtists)
                .flatMap(Stream::of)
                .map(Mapper::mapToArtist)
                .collect(Collectors.toSet());
    }

    public Album saveOrReset(Album album) {


        if (albumRepository.existWithSpotifyId(album.getSpotifyId())) {
            if (!albumRepository.existsBySpotifyIdAndChangedTrue(album.getSpotifyId())) {
                logger.debug("reset data");
                var foundAlbum = albumRepository.findBySpotifyId(album.getSpotifyId());
                foundAlbum.setName(album.getName());
                foundAlbum.setSpotifyId(album.getSpotifyId());
                foundAlbum.setAlbumGroup(album.getAlbumGroup());
                foundAlbum.setArtist(album.getArtist());
                foundAlbum.setReleaseDate(album.getReleaseDate());
                foundAlbum.setType(album.getType());

                return albumRepository.save(foundAlbum);
            } else {
                logger.debug("no change");
                return albumRepository.findBySpotifyId(album.getSpotifyId());
            }
        } else {
            return albumRepository.save(album);
        }

    }

    public Artist saveOrReset(final Artist artist) {

        if (!artistsRepository.existArtistWithSpotifyId(artist.getSpotifyId())) {
            return artistsRepository.save(artist);
        } else {
            logger.debug("no change");
            return artistsRepository.findBySpotifyId(artist.getSpotifyId());
        }
    }

}
