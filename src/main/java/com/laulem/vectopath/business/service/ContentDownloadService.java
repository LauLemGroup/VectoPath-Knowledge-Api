package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.exception.DownloadInterruptedException;
import com.laulem.vectopath.business.exception.HttpDownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class ContentDownloadService {

    public static final int TIMEOUT_DELAY_SEC = 60;
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
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(TIMEOUT_DELAY_SEC)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                logger.info("Content successfully downloaded from: {}", url);
                return response.body();
            } else {
                throw new HttpDownloadException(response.statusCode(), url);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DownloadInterruptedException(url, e);
        }
    }
}
