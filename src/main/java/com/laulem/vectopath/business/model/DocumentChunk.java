package com.laulem.vectopath.business.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modèle de domaine représentant un chunk vectorisé d'une ressource
 */
public class DocumentChunk {
    private UUID id;
    private UUID resourceId;
    private String content;
    private String metadata;
    private LocalDateTime createdAt;

    public DocumentChunk() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
