package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.service.DocumentChunkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service de gestion des chunks de documents
 * Basée sur VectorizedResourceService (RagConfig + CustomVectorStoreWithAccessControl)
 */
@Service
public class DocumentChunkServiceImpl implements DocumentChunkService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentChunkServiceImpl.class);

    private final VectorizedResourceService vectorizedResourceService;

    public DocumentChunkServiceImpl(VectorizedResourceService vectorizedResourceService) {
        this.vectorizedResourceService = vectorizedResourceService;
    }

    @Override
    public List<DocumentChunk> searchSimilarChunks(String query, int limit) {
        logger.info("Recherche de chunks similaires pour la requête : {}", query);

        // Utiliser VectorizedResourceService qui gère automatiquement la vectorisation
        // (basé sur CustomVectorStoreWithAccessControl avec EmbeddingModel + PGobject)
        return vectorizedResourceService.searchSimilar(query, limit);
    }
}
