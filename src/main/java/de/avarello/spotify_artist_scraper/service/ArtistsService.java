package de.avarello.spotify_artist_scraper.service;

import de.avarello.spotify_artist_scraper.Mapper;
import de.avarello.spotify_artist_scraper.domain.Artist;
import de.avarello.spotify_artist_scraper.model.ArtistsDTO;
import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadArtistsDTO;
import de.avarello.spotify_artist_scraper.repos.AlbumRepository;
import de.avarello.spotify_artist_scraper.repos.ArtistsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
public class ArtistsService {
    private final AlbumRepository albumRepository;
    private final ArtistsRepository artistsRepository;

    @Autowired
    public ArtistsService(final AlbumRepository albumRepository, final ArtistsRepository artistsRepository) {
        this.albumRepository = albumRepository;
        this.artistsRepository = artistsRepository;
    }

    public List<ReadArtistsDTO> findAll() {
        return artistsRepository.findAll(Sort.by("id"))
                .stream()
                .map(Mapper::mapToArtistDTO)
                .toList();
    }

    public ReadArtistsDTO getById(final UUID id) {
        return artistsRepository.findById(id)
                .map(Mapper::mapToArtistDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public UUID create(final ArtistsDTO artistsDTO) {

        if (!artistsRepository.existsByName(artistsDTO.name())) {
            final Artist artist = new Artist();

            artist.setName(artistsDTO.name());

            return artistsRepository.save(artist).getId();
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Artist Entry with this Name exist");
    }

    public void update(final UUID id, final ArtistsDTO readArtistsDTO) {

        final Artist artist = artistsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        artist.setName(readArtistsDTO.name());
        artist.setChanged(true);

        artistsRepository.save(artist);
    }


    public List<ReadAlbumDTO> getAlbumsByArtist(UUID id) {

        var foundArtist = artistsRepository.findById(id);

        return foundArtist.map(artist -> albumRepository.findDistinctByArtist(artist)
                        .stream()
                        .map(Mapper::mapToArtistDTO)
                        .toList())
                .orElse(Collections.emptyList());
    }

    public void delete(final UUID id) {
        artistsRepository.deleteById(id);
    }

}
