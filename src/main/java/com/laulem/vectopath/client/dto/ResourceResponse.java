package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResourceResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("status")
    private ResourceStatus status;

    @JsonProperty("metadata")
    private String metadata;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("content_preview")
    private String contentPreview;

    public ResourceResponse() {}

    public ResourceResponse(Resource resource) {
        this.id = resource.getId();
        this.name = resource.getName();
        this.contentType = resource.getContentType();
        this.status = resource.getStatus();
        this.metadata = resource.getMetadata();
        this.createdAt = resource.getCreatedAt();
        this.updatedAt = resource.getUpdatedAt();

        if (resource.getContent() != null) {
            this.contentPreview = resource.getContent().length() > 100
                ? resource.getContent().substring(0, 100) + "..."
                : resource.getContent();
        }
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

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }
}
