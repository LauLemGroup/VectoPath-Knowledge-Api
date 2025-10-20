package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.repository.VectorRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Service métier simplifié pour la gestion vectorielle des ressources
 * Architecture hexagonale respectée : utilise le port VectorRepository
 */
@Service
public class VectorizedResourceService {

    private static final Logger logger = LoggerFactory.getLogger(VectorizedResourceService.class);

    private final VectorRepository vectorRepository;

    public VectorizedResourceService(VectorRepository vectorRepository) {
        this.vectorRepository = vectorRepository;
    }

    /**
     * Ajoute une ressource au vector store
     */
    public void addResource(Resource resource) {
        logger.info("Demande d'ajout de la ressource [{}]", resource.getName());
        vectorRepository.addResource(resource);
    }

    /**
     * Recherche sémantique
     */
    public List<DocumentChunk> searchSimilar(String query, int limit) {
        logger.info("Recherche sémantique pour : {}", query);
        return vectorRepository.searchSimilar(query, limit);
    }

    /**
     * Supprime une ressource du vector store
     */
    public void deleteResource(UUID resourceId) {
        logger.info("Demande de suppression de la ressource : {}", resourceId);
        vectorRepository.deleteResource(resourceId);
    }

    /**
     * Vérifie si une ressource est déjà chargée
     */
    public boolean isResourceAlreadyLoaded(UUID resourceId) {
        return vectorRepository.isResourceAlreadyLoaded(resourceId);
    }
}


