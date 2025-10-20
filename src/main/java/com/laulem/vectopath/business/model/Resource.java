package com.laulem.vectopath.business.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Resource {
    private UUID id;
    private String name;
    private String content;
    private String contentType;
    private ResourceStatus status;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Resource() {
        this.id = UUID.randomUUID();
        this.status = ResourceStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Resource(String name, String content, String contentType, String metadata) {
        this();
        this.name = name;
        this.content = content;
        this.contentType = contentType;
        this.metadata = metadata;
    }

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
        this.updatedAt = LocalDateTime.now();
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
