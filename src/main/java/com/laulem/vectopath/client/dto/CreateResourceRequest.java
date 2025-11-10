package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateResourceRequest(
        @JsonProperty("name")
        String name,

        @JsonProperty("content")
        String content,

        @JsonProperty("url")
        String url,

        @JsonProperty("source_type")
        SourceType sourceType,

        @JsonProperty("content_type")
        String contentType,

        @JsonProperty("metadata")
        String metadata
) {
    public enum SourceType {
        TEXT,
        URL,
        FILE
    }
}
