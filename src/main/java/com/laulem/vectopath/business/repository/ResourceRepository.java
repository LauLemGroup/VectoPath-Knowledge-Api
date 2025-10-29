package com.laulem.vectopath.business.repository;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourceRepository {

    Resource save(Resource resource);

    Optional<Resource> findById(UUID id);

    List<Resource> findAll();

    List<Resource> findByStatus(ResourceStatus status);

    List<Resource> findByNameContainingIgnoreCase(String name);

    void deleteById(UUID id);
}
