package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.util.Map;

/**
 * Interface pour la création de ressources depuis du texte
 */
public interface TextResourceCreationService {

    /**
     * Crée une ressource depuis du contenu textuel
     */
    Resource createFromText(String name, String content, String metadata);
}
