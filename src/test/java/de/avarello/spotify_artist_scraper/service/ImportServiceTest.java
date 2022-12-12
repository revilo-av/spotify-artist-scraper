package de.avarello.spotify_artist_scraper.service;

import de.avarello.spotify_artist_scraper.model.ReadAlbumDTO;
import de.avarello.spotify_artist_scraper.model.ReadArtistsDTO;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application.test.properties")
class ImportServiceTest {
    @Autowired
    protected Flyway flyway;

    @LocalServerPort
    private int port;

    @Autowired
    private ImportService importService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ArtistsService artistsService;

    @BeforeEach
    public void initEach() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("Import Property File Artists")
    void canImportFileData() {
        importService.importArtistPropertyFile();

        List<ReadAlbumDTO> albums = albumService.findAll();

        Assertions.assertThat(albums)
                .hasSize(100);

        List<ReadArtistsDTO> artists = artistsService.findAll();
        Assertions.assertThat(artists)
                .hasSize(13);
    }


    @Test
    @DisplayName("Import Property File Artists")
    void refreshImportFileData() {
        importService.importArtistPropertyFile();

        List<ReadAlbumDTO> albums = albumService.findAll();

        Assertions.assertThat(albums)
                .hasSize(100);

        List<ReadArtistsDTO> artists = artistsService.findAll();
        Assertions.assertThat(artists)
                .hasSize(13);


        importService.importArtistPropertyFile();

        albums = albumService.findAll();

        Assertions.assertThat(albums)
                .hasSize(100);

        artists = artistsService.findAll();
        Assertions.assertThat(artists)
                .hasSize(13);

    }
}