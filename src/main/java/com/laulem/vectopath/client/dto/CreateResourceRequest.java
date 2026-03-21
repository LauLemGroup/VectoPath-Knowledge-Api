package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laulem.vectopath.business.model.Resource;

import java.util.List;

public record CreateResourceRequest(
        @JsonProperty("name")
        String name,

        @JsonProperty("content")
        String content,

        @JsonProperty("url")
        String url,

        @JsonProperty("source_type")
        SourceType sourceType,

        @JsonProperty("metadata")
        String metadata,

        @JsonProperty("access_level")
        Resource.AccessLevel accessLevel,

        @JsonProperty("allowed_roles")
        List<String> allowedRoles
) {
    public enum SourceType {
        TEXT,
        URL,
        FILE
    }
}
