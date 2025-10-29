package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, UUID> {
    List<ResourceEntity> findByStatus(ResourceStatus status);
    List<ResourceEntity> findByNameContainingIgnoreCase(String name);
}
