package com.laulem.vectopath.business.service;

import com.laulem.vectopath.business.model.DocumentChunk;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkService {

    List<DocumentChunk> searchSimilarChunks(String query, int limit);
}
