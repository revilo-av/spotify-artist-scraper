package de.avarello.spotify_artist_scraper.controller;

import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.AlbumDTO;
import de.avarello.spotify_artist_scraper.service.AlbumService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/album", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlbumResource {

    private final AlbumService albumService;

    @Autowired
    public AlbumResource(final AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<ReadAlbumDTO>> getAllAlbums() {
        return ResponseEntity.ok(albumService.findAll());
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createAlbum(@RequestBody final AlbumDTO albumDTO) {
        return new ResponseEntity<>(albumService.create(albumDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadAlbumDTO> getAlbum(@PathVariable final UUID id) {
        return ResponseEntity.ok(albumService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable final UUID id,
                                            @RequestBody @Valid final AlbumDTO albumDTO) {
        albumService.update(id, albumDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAlbum(@PathVariable final UUID id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
