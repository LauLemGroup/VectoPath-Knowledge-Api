package com.laulem.vectopath.business.model;

/**
 * Énumération des statuts possibles pour une ressource
 */
public enum ResourceStatus {
    PENDING,        // En attente de traitement
    PROCESSING,     // En cours de traitement/vectorisation
    VECTORIZED,     // Vectorisée avec succès
    ERROR          // Erreur lors du traitement
}
