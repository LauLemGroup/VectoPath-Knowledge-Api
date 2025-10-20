package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.FileResourceCreationService;
import com.laulem.vectopath.business.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implémentation du service de création de ressources depuis un fichier
 */
@Service
public class FileResourceCreationServiceImpl implements FileResourceCreationService {

    private static final Logger logger = LoggerFactory.getLogger(FileResourceCreationServiceImpl.class);

    private final ResourceService resourceService;

    public FileResourceCreationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Resource createFromFileContent(String name, byte[] fileContent, String originalFilename, String metadata) {
        validateInput(name, fileContent, originalFilename);
        logger.info("Création d'une ressource depuis le fichier : {}", originalFilename);

        try {
            String content = new String(fileContent, "UTF-8");
            return resourceService.createResource(name.trim(), content, "text/plain", metadata);
        } catch (Exception e) {
            logger.error("Erreur lors de la lecture du fichier : {}", originalFilename, e);
            throw new RuntimeException("Impossible de lire le contenu du fichier", e);
        }
    }


    private void validateInput(String name, byte[] fileContent, String originalFilename) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ressource est obligatoire");
        }
        if (fileContent == null || fileContent.length == 0) {
            throw new IllegalArgumentException("Le fichier est obligatoire pour une ressource de type FILE");
        }
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Seuls les fichiers .txt sont acceptés");
        }
    }
}
