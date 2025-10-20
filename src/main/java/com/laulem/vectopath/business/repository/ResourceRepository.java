package com.laulem.vectopath.business.repository;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port pour la gestion des ressources
 */
public interface ResourceRepository {

    /**
     * Sauvegarde une ressource
     */
    Resource save(Resource resource);

    /**
     * Recherche une ressource par son ID
     */
    Optional<Resource> findById(UUID id);

    /**
     * Recherche toutes les ressources
     */
    List<Resource> findAll();

    /**
     * Recherche les ressources par statut
     */
    List<Resource> findByStatus(ResourceStatus status);

    /**
     * Recherche les ressources par nom (recherche partielle)
     */
    List<Resource> findByNameContaining(String name);

    /**
     * Supprime une ressource
     */
    void deleteById(UUID id);

    /**
     * Vérifie si une ressource existe
     */
    boolean existsById(UUID id);
}
