package de.avarello.spotify_artist_scraper.service;

import de.avarello.spotify_artist_scraper.Mapper;
import de.avarello.spotify_artist_scraper.domain.Album;
import de.avarello.spotify_artist_scraper.model.AlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.repos.AlbumRepository;
import de.avarello.spotify_artist_scraper.repos.ArtistsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistsRepository artistsRepository;

    @Autowired
    public AlbumService(final AlbumRepository albumRepository,
                        final ArtistsRepository artistsRepository) {
        this.albumRepository = albumRepository;
        this.artistsRepository = artistsRepository;
    }

    public List<ReadAlbumDTO> findAll() {
        return albumRepository.findAll(Sort.by("id"))
                .stream()
                .map(Mapper::mapToArtistDTO)
                .toList();
    }

    public ReadAlbumDTO getById(final UUID id) {
        return albumRepository.findById(id)
                .map(Mapper::mapToArtistDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public UUID create(final AlbumDTO albumDTO) {
        if (!albumRepository.existWithSpotifyId(albumDTO.spotifyId())) {
            final Album album = new Album();
            Mapper.mapToAlbum(albumDTO, album);
            updateArtist(albumDTO.artistId(), album);
            return albumRepository.save(album).getId();
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Entry with exist with this Id");
    }


    public void update(final UUID id, final AlbumDTO albumDTO) {

        final Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Mapper.mapToAlbum(albumDTO, album);
        updateArtist(albumDTO.artistId(), album);
        album.setChanged(true);
        albumRepository.save(album);
    }

    public void delete(final UUID id) {
        albumRepository.deleteById(id);
    }


    private void updateArtist(UUID artistId, Album album) {

        if (artistId != null) {
            final var foundArtist = artistsRepository.findById(artistId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found"));

            album.setArtist(foundArtist);
        }
    }
}
