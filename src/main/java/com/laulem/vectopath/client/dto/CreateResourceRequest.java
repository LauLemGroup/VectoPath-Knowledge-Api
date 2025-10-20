package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateResourceRequest {

    public enum SourceType {
        TEXT,
        URL,
        FILE
    }

    @JsonProperty("name")
    private String name;

    @JsonProperty("content")
    private String content;

    @JsonProperty("url")
    private String url;

    @JsonProperty("source_type")
    private SourceType sourceType;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("metadata")
    private String metadata;

    public CreateResourceRequest() {}

    public CreateResourceRequest(String name, String content, String contentType, String metadata) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
        this.metadata = metadata;
        this.sourceType = SourceType.TEXT;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
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
}
