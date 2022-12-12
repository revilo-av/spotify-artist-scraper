package de.avarello.spotify_artist_scraper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "spotify")
@ConfigurationPropertiesScan
public class SpotifyConfig {

    private String clientSecret;

    private String clientId;

    private Integer fetchAlbumSize;

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getFetchAlbumSize() {
        return fetchAlbumSize;
    }

    public void setFetchAlbumSize(Integer fetchAlbumSize) {
        this.fetchAlbumSize = fetchAlbumSize;
    }
}
