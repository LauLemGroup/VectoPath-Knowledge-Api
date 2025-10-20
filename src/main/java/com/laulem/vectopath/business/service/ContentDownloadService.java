package com.laulem.vectopath.business.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class ContentDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(ContentDownloadService.class);

    private final HttpClient httpClient;

    public ContentDownloadService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String downloadContent(String url) throws IOException {
        logger.info("Downloading content from URL: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Content successfully downloaded from: {}", url);
                return response.body();
            } else {
                throw new IOException("HTTP error " + response.statusCode() + " when downloading from " + url);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrupted for " + url, e);
        } catch (Exception e) {
            logger.error("Error downloading from URL: {}", url, e);
            throw new IOException("Error downloading from " + url, e);
        }
    }
}
