package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.service.DocumentChunkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentChunkServiceImpl implements DocumentChunkService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentChunkServiceImpl.class);

    private final VectorizedResourceService vectorizedResourceService;

    public DocumentChunkServiceImpl(VectorizedResourceService vectorizedResourceService) {
        this.vectorizedResourceService = vectorizedResourceService;
    }

    @Override
    public List<DocumentChunk> searchSimilarChunks(String query, int limit) {
        logger.info("Searching similar chunks for query: {}", query);

        return vectorizedResourceService.searchSimilar(query, limit);
    }
}
