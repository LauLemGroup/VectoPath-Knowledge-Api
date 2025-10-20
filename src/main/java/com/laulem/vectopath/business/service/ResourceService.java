package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface du service de gestion des ressources
 */
public interface ResourceService {

    /**
     * Crée une nouvelle ressource et lance sa vectorisation
     */
    Resource createResource(String name, String content, String contentType, String metadata);

    /**
     * Récupère une ressource par son ID
     */
    Optional<Resource> getResourceById(UUID id);

    /**
     * Récupère toutes les ressources
     */
    List<Resource> getAllResources();

    /**
     * Récupère les ressources par statut
     */
    List<Resource> getResourcesByStatus(ResourceStatus status);

    /**
     * Recherche des ressources par nom
     */
    List<Resource> searchResourcesByName(String name);

    /**
     * Met à jour le statut d'une ressource
     */
    Resource updateResourceStatus(UUID id, ResourceStatus status);

    /**
     * Supprime une ressource et ses chunks associés
     */
    void deleteResource(UUID id);

    /**
     * Relance la vectorisation d'une ressource
     */
    Resource reprocessResource(UUID id);
}
