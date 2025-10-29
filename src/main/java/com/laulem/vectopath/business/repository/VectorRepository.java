package com.laulem.vectopath.business.repository;

import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.model.Resource;

import java.util.List;
import java.util.UUID;

public interface VectorRepository {

    void addResource(Resource resource);

    List<DocumentChunk> searchSimilar(String query, int limit);

    List<DocumentChunk> getChunksByResourceId(UUID resourceId);

    void deleteResource(UUID resourceId);

    boolean isResourceAlreadyLoaded(UUID resourceId);
}
