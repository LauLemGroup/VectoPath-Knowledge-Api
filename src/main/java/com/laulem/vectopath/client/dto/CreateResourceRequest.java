package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour la création d'une nouvelle ressource
 * Peut accepter du contenu direct, une URL ou être utilisé avec un fichier
 */
public class CreateResourceRequest {

    public enum SourceType {
        TEXT,    // Contenu textuel direct
        URL,     // URL à télécharger
        FILE     // Fichier uploadé (géré par le contrôleur)
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

    // Getters et Setters
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
