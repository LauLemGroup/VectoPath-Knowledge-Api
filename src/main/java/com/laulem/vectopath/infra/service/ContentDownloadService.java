package com.laulem.vectopath.infra.service;

import com.laulem.vectopath.business.exception.DownloadInterruptedException;
import com.laulem.vectopath.business.exception.HttpDownloadException;
import com.laulem.vectopath.business.service.ContentDownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class ContentDownloadService implements ContentDownloaderService {

    private static final Logger logger = LoggerFactory.getLogger(ContentDownloadService.class);
    private final HttpClient httpClient;
    private final int timeoutSeconds;

    public ContentDownloadService(
            @Value("${content.download.timeout-seconds:30}") int timeoutSeconds,
            @Value("${content.download.connect-timeout-seconds:10}") int connectTimeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectTimeoutSeconds))
                .build();
    }

    @Override
    public String downloadContent(String url) throws IOException {
        logger.info("Downloading content from URL: {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .GET()
                    .build();

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

