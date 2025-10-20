package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.DocumentChunk;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des chunks de documents
 */
public interface DocumentChunkService {

    /**
     * Recherche des chunks similaires à partir d'un texte de requête
     */
    List<DocumentChunk> searchSimilarChunks(String query, int limit);
}
