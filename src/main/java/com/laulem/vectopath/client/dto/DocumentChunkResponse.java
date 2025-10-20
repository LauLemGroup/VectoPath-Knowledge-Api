package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laulem.vectopath.business.model.DocumentChunk;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour les réponses de recherche de chunks de documents
 */
public class DocumentChunkResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("resource_id")
    private UUID resourceId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("metadata")
    private String metadata;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public DocumentChunkResponse() {}

    public DocumentChunkResponse(DocumentChunk chunk) {
        this.id = chunk.getId();
        this.resourceId = chunk.getResourceId();
        this.content = chunk.getContent();
        this.metadata = chunk.getMetadata();
        this.createdAt = chunk.getCreatedAt();
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
