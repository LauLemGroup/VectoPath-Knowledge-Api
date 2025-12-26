package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.PartialResource;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.repository.VectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VectorizedResourceService {

    private static final Logger logger = LoggerFactory.getLogger(VectorizedResourceService.class);

    private final VectorRepository vectorRepository;

    public VectorizedResourceService(VectorRepository vectorRepository) {
        this.vectorRepository = vectorRepository;
    }

    public void addResource(Resource resource) {
        logger.info("Adding resource [{}]", resource.getName());
        vectorRepository.addResource(resource);
    }

    public List<PartialResource> searchSimilar(String query, int limit) {
        logger.info("Semantic search for: {}", query);
        return vectorRepository.searchSimilar(query, limit);
    }

    public void deleteResource(UUID resourceId) {
        logger.info("Deleting resource: {}", resourceId);
        vectorRepository.deleteResource(resourceId);
    }

    public boolean isResourceAlreadyLoaded(UUID resourceId) {
        return vectorRepository.isResourceAlreadyLoaded(resourceId);
    }
}


