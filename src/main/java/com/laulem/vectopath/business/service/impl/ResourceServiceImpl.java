package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.exception.NotFoundException;
import com.laulem.vectopath.business.exception.VectorizationException;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.repository.ResourceRepository;
import com.laulem.vectopath.business.repository.VectorStoreRepository;
import com.laulem.vectopath.business.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceRepository resourceRepository;
    private final VectorizedResourceService vectorizedResourceService;
    private final VectorStoreRepository vectorRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               VectorizedResourceService vectorizedResourceService,
                               VectorStoreRepository vectorRepository) {
        this.resourceRepository = resourceRepository;
        this.vectorizedResourceService = vectorizedResourceService;
        this.vectorRepository = vectorRepository;
    }

    @Override
    public Resource createResource(Resource resource) {
        if (resource.getAccessLevel() == null) {
            resource.setAccessLevel(Resource.AccessLevel.PRIVATE);
        }
        resource = processResourceVectorization(resource);
        return resource;
    }

    @Override
    public Optional<Resource> getResourceById(UUID id) {
        return resourceRepository.findById(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public List<Resource> getResourcesByStatus(ResourceStatus status) {
        return resourceRepository.findByStatus(status);
    }

    @Override
    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void deleteResource(UUID id) {
        logger.info("Deleting resource: {}", id);

        vectorizedResourceService.deleteResource(id);
        resourceRepository.deleteById(id);
    }

    @Override
    public Resource reprocessResource(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource", id.toString()));

        logger.info("Reprocessing resource: {}", resource.getName());

        vectorizedResourceService.deleteResource(id);
        resource = processResourceVectorization(resource);

        return resource;
    }

    private Resource processResourceVectorization(Resource resource) {
        try {
            resource.setStatus(ResourceStatus.PROCESSING);
            resource = resourceRepository.save(resource);

            vectorRepository.addResource(resource);

            resource.setStatus(ResourceStatus.VECTORIZED);
            resourceRepository.updateStatus(resource);

            logger.info("Vectorization completed successfully for resource: {}", resource.getName());
            return resource;
        } catch (Exception e) {
            resource.setStatus(ResourceStatus.ERROR);
            resourceRepository.updateStatus(resource);

            throw new VectorizationException(resource.getName(), e);
        }
    }
}
