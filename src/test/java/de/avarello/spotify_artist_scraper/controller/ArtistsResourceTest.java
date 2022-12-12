package de.avarello.spotify_artist_scraper.controller;

import de.avarello.spotify_artist_scraper.model.ArtistsDTO;
import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadArtistsDTO;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application.test.properties")
class ArtistsResourceTest {
    private String baseUrl = "http://localhost:";

    private HttpHeaders headers;

    @Autowired
    protected Flyway flyway;

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void initEach() {

        baseUrl = baseUrl + port + "/api/";

        // init default header
        headers = new HttpHeaders();
        headers.set("accept", "application/json");

        flyway.clean();
        flyway.migrate();
    }


    @Test
    @DisplayName("can import Artist albums from Spotify by Artist id")
    void canImportAlbumsFromSpotifyByArtistId() throws Exception {

        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<ReadAlbumDTO[]> artistsAlbumsRequest = getAllAlbumFromArtistRequest(createdArtistId);

        assumeThat(artistsAlbumsRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadAlbumDTO[].class)
                .hasSize(10);
    }


    @Test
    @DisplayName("can delete artist with albums by Id")
    void canDelete() throws Exception {
        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        restTemplate.delete(new URI(baseUrl + "artists/" + createdArtistId.getBody()));

        ResponseEntity<ReadAlbumDTO[]> artistsAlbumsRequest = getAllAlbumFromArtistRequest(createdArtistId);

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadAlbumDTO[].class)
                .isEmpty();
    }


    @Test
    @DisplayName("can Update Artist Name")
    void canUpdateArtist() throws Exception {
        final String importId = "33k6kPYIS5TgseAc70LZjy";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        ResponseEntity<ReadArtistsDTO> artistsAlbumsRequest = getArtistById(createdArtistId.getBody());

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadArtistsDTO.class);


        String newName = "TestArtist";

        ResponseEntity<Void> updateResponse = updateArtistRequest(createdArtistId, newName);

        assumeThat(updateResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<ReadArtistsDTO> artistsAlbumsRequestAfterUpdate = getArtistById(createdArtistId.getBody());

        assertThat(artistsAlbumsRequestAfterUpdate.getBody())
                .isInstanceOfAny(ReadArtistsDTO.class)
                .extracting("name")
                .isEqualTo(newName);
    }


    @Test
    @DisplayName("get All Artists ")
    void getAllArtists() throws Exception {

        final List<String> artistIds = List.of(
                "33k6kPYIS5TgseAc70LZjy",
                "0cXE2SrE0M4Vn7F2NVzG6q"
        );

        ResponseEntity<UUID> firstCreatedArtist = importAlbumRequest(artistIds.get(0));

        assumeThat(firstCreatedArtist.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        ResponseEntity<UUID> secondCreatedArtist = importAlbumRequest(artistIds.get(1));

        assumeThat(secondCreatedArtist.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        ResponseEntity<ReadArtistsDTO[]> artistsAlbumsRequest = restTemplate.getForEntity(
                new URI(baseUrl + "artists"),
                ReadArtistsDTO[].class
        );

        assumeThat(artistsAlbumsRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadArtistsDTO[].class)
                .hasSize(8);
    }

    @Test
    @DisplayName("can Create new Artist")
    void createNewArtist() {

        String newName = "TestArtist";

        ResponseEntity<UUID> createdArtist = createNewArtistRequest(newName);

        assumeThat(createdArtist.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);


        ResponseEntity<ReadArtistsDTO> artistsAlbumsRequest = getArtistById(createdArtist.getBody());

        assumeThat(artistsAlbumsRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadArtistsDTO.class);
    }

    private ResponseEntity<UUID> createNewArtistRequest(String newName) {
        return restTemplate.exchange(
                baseUrl + "artists",
                HttpMethod.POST,
                new HttpEntity<>(new ArtistsDTO(newName), headers),
                UUID.class
        );
    }


    private ResponseEntity<UUID> importAlbumRequest(String importId) throws URISyntaxException {
        final String importUrl = "artists/import/";

        URI importAlbumByArtistRequest = new URI(baseUrl + importUrl + importId);

        return restTemplate.postForEntity(
                importAlbumByArtistRequest,
                new HttpEntity<>(null, headers),
                UUID.class
        );
    }


    private ResponseEntity<ReadAlbumDTO[]> getAllAlbumFromArtistRequest(ResponseEntity<UUID> createdArtistId) throws URISyntaxException {
        return restTemplate.getForEntity(
                new URI(baseUrl + "artists/" + createdArtistId.getBody() + "/albums"),
                ReadAlbumDTO[].class
        );
    }

    private ResponseEntity<Void> updateArtistRequest(ResponseEntity<UUID> createdArtistId, String newName) {
        return restTemplate.exchange(
                baseUrl + "artists/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new ArtistsDTO(newName), headers),
                Void.class,
                createdArtistId.getBody()
        );
    }

    private ResponseEntity<ReadArtistsDTO> getArtistById(UUID artistId) {
        return restTemplate.getForEntity(
                baseUrl + "artists/" + artistId,
                ReadArtistsDTO.class
        );
    }


}