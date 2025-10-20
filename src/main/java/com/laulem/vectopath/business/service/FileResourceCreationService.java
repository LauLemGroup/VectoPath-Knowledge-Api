package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.util.Map;

/**
 * Interface pour la création de ressources depuis un fichier
 */
public interface FileResourceCreationService {

    /**
     * Crée une ressource depuis le contenu d'un fichier
     */
    Resource createFromFileContent(String name, byte[] fileContent, String originalFilename, String metadata);
}
