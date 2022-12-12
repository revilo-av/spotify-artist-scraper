package de.avarello.spotify_artist_scraper;

import de.avarello.spotify_artist_scraper.domain.Album;
import de.avarello.spotify_artist_scraper.domain.Artist;
import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadArtistsDTO;
import de.avarello.spotify_artist_scraper.model.AlbumDTO;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

public class Mapper {
    private Mapper() {
    }

    public static ReadAlbumDTO mapToArtistDTO(final Album album) {
        ReadArtistsDTO readArtistsDTO = null;
        if (album.getArtist() != null) {
            readArtistsDTO = new ReadArtistsDTO(
                    album.getArtist().getId(),
                    album.getArtist().getName(),
                    album.getArtist().getSpotifyId(),
                    album.getArtist().getCreateDate(),
                    album.getArtist().getLastUpdated()
            );
        }
        return new ReadAlbumDTO(
                album.getId(),
                album.getName(),
                readArtistsDTO,
                album.getAlbumGroup(),
                album.getAlbumType(),
                album.getReleaseDate(),
                album.getType(),
                album.getSpotifyId(),
                album.getCreateDate(),
                album.getLastUpdated()
        );
    }

    public static ReadArtistsDTO mapToArtistDTO(final Artist artist) {
        return new ReadArtistsDTO(
                artist.getId(),
                artist.getName(),
                artist.getSpotifyId(),
                artist.getCreateDate(),
                artist.getLastUpdated()
        );
    }

    public static void mapToAlbum(final AlbumDTO albumDTO, final Album album) {
        album.setName(albumDTO.name());
        album.setAlbumGroup(albumDTO.albumGroup());
        album.setAlbumType(albumDTO.albumType());
        album.setReleaseDate(albumDTO.releaseDate());
        album.setSpotifyId(albumDTO.spotifyId());
        album.setType(albumDTO.type());
    }


    public static Album mapToAlbum(AlbumSimplified albumSimplified) {

        Album album = new Album();
        album.setName(albumSimplified.getName());
        album.setReleaseDate(albumSimplified.getReleaseDate());
        album.setAlbumGroup(albumSimplified.getAlbumGroup().group);
        album.setAlbumType(albumSimplified.getAlbumType().type);
        album.setSpotifyId(albumSimplified.getId());
        album.setType(albumSimplified.getType().type);
        return album;
    }


    public static Artist mapToArtist(ArtistSimplified artistSimplified) {
        Artist artist = new Artist();
        artist.setName(artistSimplified.getName());
        artist.setSpotifyId(artistSimplified.getId());
        return artist;
    }


}
