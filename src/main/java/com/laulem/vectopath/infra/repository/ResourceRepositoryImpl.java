package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.repository.ResourceRepository;
import com.laulem.vectopath.business.service.AuthenticationService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    private final ResourceJpaRepository jpaRepository;
    private final AuthenticationService authenticationService;

    public ResourceRepositoryImpl(ResourceJpaRepository jpaRepository, AuthenticationService authenticationService) {
        this.jpaRepository = jpaRepository;
        this.authenticationService = authenticationService;
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
        String username = authenticationService.getUser().orElse(null);
        List<String> userRoles = authenticationService.getAuthorities();

        List<ResourceEntity> results = jpaRepository.findWithAccessControl(
            id.toString(),
            null,
            null,
            username,
            userRoles.toArray(new String[0])
        );

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0).toDomain());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findAll() {
        String username = authenticationService.getUser().orElse(null);
        List<String> userRoles = authenticationService.getAuthorities();

        return jpaRepository.findWithAccessControl(
            null,
            null,
            null,
            username,
            userRoles.toArray(new String[0])
        ).stream()
        .map(ResourceEntity::toDomain)
        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findByStatus(ResourceStatus status) {
        String username = authenticationService.getUser().orElse(null);
        List<String> userRoles = authenticationService.getAuthorities();

        return jpaRepository.findWithAccessControl(
            null,
            status.name(),
            null,
            username,
            userRoles.toArray(new String[0])
        ).stream()
        .map(ResourceEntity::toDomain)
        .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> findByNameContainingIgnoreCase(String name) {
        String username = authenticationService.getUser().orElse(null);
        List<String> userRoles = authenticationService.getAuthorities();

        return jpaRepository.findWithAccessControl(
            null,
            null,
            name,
            username,
            userRoles.toArray(new String[0])
        ).stream()
        .map(ResourceEntity::toDomain)
        .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
