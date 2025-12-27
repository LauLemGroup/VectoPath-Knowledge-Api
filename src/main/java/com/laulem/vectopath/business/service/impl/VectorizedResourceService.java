package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.PartialResource;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.repository.ResourceRepository;
import com.laulem.vectopath.business.repository.VectorRepository;
import com.laulem.vectopath.business.service.ResourceAccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VectorizedResourceService {

    private static final Logger logger = LoggerFactory.getLogger(VectorizedResourceService.class);

    private final VectorRepository vectorRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAccessControlService accessControlService;

    public VectorizedResourceService(VectorRepository vectorRepository,
                                    ResourceRepository resourceRepository,
                                    ResourceAccessControlService accessControlService) {
        this.vectorRepository = vectorRepository;
        this.resourceRepository = resourceRepository;
        this.accessControlService = accessControlService;
    }

    public void addResource(Resource resource) {
        logger.info("Adding resource [{}]", resource.getName());
        vectorRepository.addResource(resource);
    }

    public List<PartialResource> searchSimilar(String query, int limit) {
        logger.info("Semantic search for: {}", query);
        List<PartialResource> results = vectorRepository.searchSimilar(query, limit);

        // Filtrer les résultats en fonction des droits d'accès
        return results.stream()
                .filter(partialResource -> {
                    if (partialResource.getResourceId() == null) {
                        return true; // Si pas de resourceId, on laisse passer
                    }
                    return resourceRepository.findById(partialResource.getResourceId())
                            .map(accessControlService::hasAccess)
                            .orElse(false);
                })
                .toList();
    }

    public void deleteResource(UUID resourceId) {
        logger.info("Deleting resource: {}", resourceId);
        vectorRepository.deleteResource(resourceId);
    }

    public boolean isResourceAlreadyLoaded(UUID resourceId) {
        return vectorRepository.isResourceAlreadyLoaded(resourceId);
    }
}


