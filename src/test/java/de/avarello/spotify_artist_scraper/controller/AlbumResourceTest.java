package de.avarello.spotify_artist_scraper.controller;

import de.avarello.spotify_artist_scraper.model.AlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application.test.properties")
class AlbumResourceTest {

    private String baseUrl = "http://localhost:";

    private HttpHeaders headers;

    @LocalServerPort
    private int port;

    @Autowired
    protected Flyway flyway;

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
    void canGetAllAlbums() throws Exception {

        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);


        ResponseEntity<ReadAlbumDTO[]> artistsAlbumsRequest = getAllAlbumRequests();

        assumeThat(artistsAlbumsRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(artistsAlbumsRequest.getBody())
                .isInstanceOfAny(ReadAlbumDTO[].class)
                .hasSize(10);
    }


    @Test
    @DisplayName("can create new Album")
    void canCreate() throws Exception {

        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        var newAlbum = getNewAlbumDummy(createdArtistId, "New Album");

        ResponseEntity<UUID> createdArtist = createNewAlbumRequest(newAlbum);

        assumeThat(createdArtist.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);


        ResponseEntity<ReadAlbumDTO> getAlbumByIdRequest = getAlbumByIdRequest(createdArtist);

        assumeThat(getAlbumByIdRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(getAlbumByIdRequest.getBody())
                .isInstanceOfAny(ReadAlbumDTO.class)
                .hasFieldOrPropertyWithValue("name", newAlbum.name());
    }


    @Test
    @DisplayName("can delete Album")
    void canDelete() throws Exception {

        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<UUID> createdAlbumResponse = createNewAlbumRequest(getNewAlbumDummy(createdArtistId, "New Album"));

        assumeThat(createdAlbumResponse.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);


        restTemplate.delete(new URI(baseUrl + "album/" + createdAlbumResponse.getBody()));


        ResponseEntity<ReadAlbumDTO> getAlbumByIdRequest = getAlbumByIdRequest(createdAlbumResponse);


        assertThat(getAlbumByIdRequest.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("can Album Update")
    void canUpdate() throws Exception {

        final String importId = "0cXE2SrE0M4Vn7F2NVzG6q";

        ResponseEntity<UUID> createdArtistId = importAlbumRequest(importId);

        assumeThat(createdArtistId.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<UUID> createdAlbumResponse = createNewAlbumRequest(
                getNewAlbumDummy(createdArtistId, "New Album")
        );

        assumeThat(createdAlbumResponse.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        ResponseEntity<ReadAlbumDTO> getAlbumByIdRequest = getAlbumByIdRequest(createdAlbumResponse);

        assertThat(getAlbumByIdRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        var updatedAlbum = getNewAlbumDummy(createdArtistId, "Updated Album");


        ResponseEntity<Void> updateResponse = updateAlbumRequest(createdAlbumResponse, updatedAlbum);

        assumeThat(updateResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        getAlbumByIdRequest = getAlbumByIdRequest(createdAlbumResponse);

        assertThat(getAlbumByIdRequest.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(getAlbumByIdRequest.getBody())
                .isInstanceOfAny(ReadAlbumDTO.class)
                .hasFieldOrPropertyWithValue("name", updatedAlbum.name());
    }


    protected ResponseEntity<UUID> importAlbumRequest(String importId) throws URISyntaxException {
        final String importUrl = "artists/import/";

        URI importAlbumByArtistRequest = new URI(baseUrl + importUrl + importId);

        return restTemplate.postForEntity(
                importAlbumByArtistRequest,
                new HttpEntity<>(null, headers),
                UUID.class
        );
    }


    protected static AlbumDTO getNewAlbumDummy(ResponseEntity<UUID> createdArtistId, String name) {
        return new AlbumDTO(
                name,
                createdArtistId.getBody(),
                "Album",
                "Album",
                "2011-01-01",
                "",
                ""
        );
    }

    protected ResponseEntity<UUID> createNewAlbumRequest(AlbumDTO newAlbum) {
        return restTemplate.exchange(
                baseUrl + "album",
                HttpMethod.POST,
                new HttpEntity<>(newAlbum, headers),
                UUID.class
        );
    }


    protected ResponseEntity<ReadAlbumDTO[]> getAllAlbumRequests() throws URISyntaxException {
        return restTemplate.getForEntity(
                new URI(baseUrl + "album"),
                ReadAlbumDTO[].class
        );
    }

    protected ResponseEntity<Void> updateAlbumRequest(ResponseEntity<UUID> createdAlbumResponse, AlbumDTO updatedAlbum) {
        return restTemplate.exchange(
                baseUrl + "album/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updatedAlbum, headers),
                Void.class,
                createdAlbumResponse.getBody()
        );
    }

    protected ResponseEntity<ReadAlbumDTO> getAlbumByIdRequest(ResponseEntity<UUID> createdArtist) throws URISyntaxException {
        return restTemplate.getForEntity(
                new URI(baseUrl + "album/" + createdArtist.getBody()),
                ReadAlbumDTO.class
        );
    }

}