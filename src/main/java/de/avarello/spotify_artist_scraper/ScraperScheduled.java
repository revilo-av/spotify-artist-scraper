package de.avarello.spotify_artist_scraper;

import de.avarello.spotify_artist_scraper.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScraperScheduled {
    private static final Logger logger = LoggerFactory.getLogger(ScraperScheduled.class);
    private final ImportService importService;

    @Autowired
    public ScraperScheduled(final ImportService importService) {
        this.importService = importService;
    }


    // indication is in seconds
    @Scheduled(fixedDelay = 10_000, initialDelay = 50_000)
    public void runAtStartup() {
        logger.info("Cronjob starts with the download of the Spotify data");
        importService.importArtistPropertyFile();
    }
}
