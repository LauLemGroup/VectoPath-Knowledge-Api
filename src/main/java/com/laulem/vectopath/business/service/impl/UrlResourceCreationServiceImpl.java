package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ContentDownloadService;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.business.service.UrlResourceCreationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Implémentation du service de création de ressources depuis une URL
 */
@Service
public class UrlResourceCreationServiceImpl implements UrlResourceCreationService {

    private static final Logger logger = LoggerFactory.getLogger(UrlResourceCreationServiceImpl.class);

    private final ResourceService resourceService;
    private final ContentDownloadService contentDownloadService;

    public UrlResourceCreationServiceImpl(ResourceService resourceService, ContentDownloadService contentDownloadService) {
        this.resourceService = resourceService;
        this.contentDownloadService = contentDownloadService;
    }

    @Override
    public Resource createFromUrl(String name, String url, String metadata) {
        validateInput(name, url);
        logger.info("Création d'une ressource depuis l'URL : {}", url);

        try {
            String content = contentDownloadService.downloadContent(url.trim());

            return resourceService.createResource(
                name.trim(),
                content,
                "text/plain",
                metadata
            );
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement depuis l'URL : {}", url, e);
            throw new RuntimeException("Impossible de télécharger le contenu depuis l'URL : " + url, e);
        }
    }


    private void validateInput(String name, String url) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ressource est obligatoire");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("L'URL est obligatoire pour une ressource de type URL");
        }
    }
}
