package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.repository.ResourceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    private final ResourceJpaRepository jpaRepository;

    public ResourceRepositoryImpl(ResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Resource save(Resource resource) {
        ResourceEntity entity = ResourceEntity.fromDomain(resource);
        ResourceEntity savedEntity = jpaRepository.save(entity);
        
        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resource> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(ResourceEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        return jpaRepository.findAll().stream()
                .map(ResourceEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findByStatus(ResourceStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(ResourceEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findByNameContainingIgnoreCase(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ResourceEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
