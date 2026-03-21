package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laulem.vectopath.business.model.PartialResource;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    @JsonProperty("vector_id")
    private UUID vectorId;

    @JsonProperty("resource_id")
    private UUID resourceId;

    @JsonProperty("resource_name")
    private String resourceName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("metadata")
    private String metadata;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("similarity_score")
    private Double similarityScore;

    public SearchResponse(PartialResource partialResource) {
        this.vectorId = partialResource.getVectorId();
        this.resourceId = partialResource.getResourceId();
        this.resourceName = partialResource.getResourceName();
        this.content = partialResource.getContent();
        this.contentType = partialResource.getContentType();
        this.metadata = partialResource.getMetadata();
        this.createdAt = partialResource.getCreatedAt();
        this.updatedAt = partialResource.getUpdatedAt();
        this.similarityScore = partialResource.getSimilarityScore();
    }

    public UUID getVectorId() {
        return vectorId;
    }

    public void setVectorId(UUID vectorId) {
        this.vectorId = vectorId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }
}
