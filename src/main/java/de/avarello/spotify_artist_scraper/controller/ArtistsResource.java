package de.avarello.spotify_artist_scraper.controller;

import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadArtistsDTO;
import de.avarello.spotify_artist_scraper.service.ArtistsService;
import de.avarello.spotify_artist_scraper.service.ImportService;
import de.avarello.spotify_artist_scraper.model.ArtistsDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/artists", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistsResource {

    private final ArtistsService artistsService;
    private final ImportService importService;

    public ArtistsResource(final ArtistsService artistsService, final ImportService importService) {
        this.artistsService = artistsService;
        this.importService = importService;
    }

    @GetMapping
    public ResponseEntity<List<ReadArtistsDTO>> getAllArtists() {
        return ResponseEntity.ok(artistsService.findAll());
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createArtist(@RequestBody @Valid final ArtistsDTO artistsDTO) {
        return new ResponseEntity<>(artistsService.create(artistsDTO), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReadArtistsDTO> getArtist(@PathVariable final UUID id) {
        return ResponseEntity.ok(artistsService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateArtist(@PathVariable final UUID id, @RequestBody @Valid final ArtistsDTO artistsDTO) {
        artistsService.update(id, artistsDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteArtist(@PathVariable final UUID id) {
        artistsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<ReadAlbumDTO>> getArtistsAlbums(@PathVariable final UUID id) {
        return ResponseEntity.ok(artistsService.getAlbumsByArtist(id));
    }

    @PostMapping("/import/{spotifyId}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> importById(@PathVariable final String spotifyId) {
        return ResponseEntity.ok(importService.importFromSpotify(spotifyId));
    }

}
