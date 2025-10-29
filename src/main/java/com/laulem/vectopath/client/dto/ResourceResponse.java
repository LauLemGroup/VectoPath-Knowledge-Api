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

    public ResourceResponse(Resource resource) {
        this.id = resource.getId();
        this.name = resource.getName();
        this.contentType = resource.getContentType();
        this.status = resource.getStatus();
        this.metadata = resource.getMetadata();
        this.createdAt = resource.getCreatedAt();
        this.updatedAt = resource.getUpdatedAt();
    }
}
