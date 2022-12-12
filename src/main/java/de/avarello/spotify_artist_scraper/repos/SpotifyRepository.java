package de.avarello.spotify_artist_scraper.repos;

import de.avarello.spotify_artist_scraper.config.SpotifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

import java.util.*;

@Service
public class SpotifyRepository {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyRepository.class);
    private final SpotifyConfig spotifyConfig;
    private SpotifyApi spotifyApi;

    public SpotifyRepository(final SpotifyConfig spotifyConfig) {
        this.spotifyConfig = spotifyConfig;
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .build();

        spotifyApi.setAccessToken(getSpotifyToken(spotifyApi));
    }

    private static String getSpotifyToken(SpotifyApi spotifyApi) {
        return spotifyApi.clientCredentials()
                .build()
                .executeAsync()
                .join()
                .getAccessToken();
    }

    public List<AlbumSimplified> getArtistById(String id) {

        try {
            var result = spotifyApi.getArtistsAlbums(id)
                    .limit(spotifyConfig.getFetchAlbumSize())
                    .build()
                    .executeAsync();

            return Arrays.asList(result.join().getItems());
        } catch (Exception exception) {
            logger.error("Error By retrieve Album data {0}", exception);
        }

        return Collections.emptyList();
    }

}
