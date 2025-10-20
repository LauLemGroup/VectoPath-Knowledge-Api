package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;

import java.util.Map;

/**
 * Interface pour la création de ressources depuis une URL
 */
public interface UrlResourceCreationService {

    /**
     * Crée une ressource depuis une URL
     */
    Resource createFromUrl(String name, String url, String metadata);
}
