package com.laulem.vectopath.infra.entity;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resources")
public class ResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_type")
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private Resource.SourceType sourceType;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "size")
    private Long size;

    @Column(name = "created_by")
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private Resource.AccessLevel accessLevel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "resource_allowed_roles",
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> allowedRoles = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public static ResourceEntity fromDomain(Resource resource) {
        ResourceEntity entity = new ResourceEntity();
        if (resource.getId() != null) {
            entity.id = resource.getId();
        }
        entity.name = resource.getName();
        entity.content = resource.getContent();
        entity.contentType = resource.getContentType();
        entity.status = resource.getStatus();
        entity.metadata = resource.getMetadata();
        entity.sourceType = resource.getSourceType();
        entity.sourceName = resource.getSourceName();
        entity.size = resource.getSize();
        entity.createdBy = resource.getCreatedBy();
        entity.accessLevel = resource.getAccessLevel();
        entity.createdAt = resource.getCreatedAt();
        entity.updatedAt = resource.getUpdatedAt();
        return entity;
    }

    public Resource toDomain() {
        Resource resource = new Resource();
        resource.setId(this.id);
        resource.setName(this.name);
        resource.setContent(this.content);
        resource.setContentType(this.contentType);
        resource.setStatus(this.status);
        resource.setMetadata(this.metadata);
        resource.setSourceType(this.sourceType);
        resource.setSourceName(this.sourceName);
        resource.setSize(this.size);
        resource.setCreatedBy(this.createdBy);
        resource.setAccessLevel(this.accessLevel);
        resource.setAllowedRoles(
                this.allowedRoles.stream()
                        .map(RoleEntity::getRoleName)
                        .toList()
        );
        resource.setCreatedAt(this.createdAt);
        resource.setUpdatedAt(this.updatedAt);
        return resource;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Resource.SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(Resource.SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Resource.AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Resource.AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<RoleEntity> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<RoleEntity> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

