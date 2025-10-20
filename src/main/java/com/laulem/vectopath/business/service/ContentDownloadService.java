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

/**
 * Service pour télécharger du contenu depuis des URLs
 */
@Service
public class ContentDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(ContentDownloadService.class);

    private final HttpClient httpClient;

    public ContentDownloadService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Télécharge le contenu depuis une URL
     * @param url L'URL à télécharger
     * @return Le contenu sous forme de String
     * @throws IOException Si une erreur survient lors du téléchargement
     */
    public String downloadContent(String url) throws IOException {
        logger.info("Téléchargement du contenu depuis l'URL : {}", url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.info("Contenu téléchargé avec succès depuis : {}", url);
                return response.body();
            } else {
                throw new IOException("Erreur HTTP " + response.statusCode() + " lors du téléchargement de " + url);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Téléchargement interrompu pour " + url, e);
        } catch (Exception e) {
            logger.error("Erreur lors du téléchargement depuis l'URL : {}", url, e);
            throw new IOException("Erreur lors du téléchargement depuis " + url, e);
        }
    }
}
