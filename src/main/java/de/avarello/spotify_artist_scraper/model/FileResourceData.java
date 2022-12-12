package de.avarello.spotify_artist_scraper.model;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileResourceData {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceData.class);
    private static final String ARTISTS_LIST_JSON_FILENAME = "artists_list.json";

    private FileResourceData() {
    }

    public static List<ArtistProperty> getArtistIds() throws IOException {
        ClassPathResource staticDataResource = new ClassPathResource(ARTISTS_LIST_JSON_FILENAME);
        String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);

        return Arrays.stream(new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(staticDataString, ArtistProperty[].class)).toList();
    }

    public static List<ArtistProperty> getArtistList() {
        List<ArtistProperty> result = new ArrayList<>();
        try {
            result = FileResourceData.getArtistIds();
        } catch (Exception exception) {
            logger.error("Error importing artist list json files {0}", exception);
        }
        return result;
    }
}

