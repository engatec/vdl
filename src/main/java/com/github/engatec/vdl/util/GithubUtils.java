package com.github.engatec.vdl.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GithubUtils {

    private static final Logger LOGGER = LogManager.getLogger(GithubUtils.class);

    public static ReleaseDto getLatestReleaseInfo(String namespace, String repository) {
        LOGGER.info("Fetching the latest version from github repository {}/{}", namespace, repository);

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.github.com/repos/%s/%s/releases/latest", namespace, repository)))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            ReleaseDto releaseInfo = new ObjectMapper().readValue(responseBody, ReleaseDto.class);
            if (StringUtils.isNotBlank(releaseInfo.message())) { // Non blank message is most likely an error message, need log it
                LOGGER.warn(releaseInfo.message());
            }
            return releaseInfo;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
