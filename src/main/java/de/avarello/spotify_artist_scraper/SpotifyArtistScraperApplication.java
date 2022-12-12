package de.avarello.spotify_artist_scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpotifyArtistScraperApplication {
    public static void main(final String[] args) {
        SpringApplication.run(SpotifyArtistScraperApplication.class, args);
    }
}
