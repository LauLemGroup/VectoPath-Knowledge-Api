package com.laulem.vectopath.infra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {
    private EmbeddingProviderType provider = EmbeddingProviderType.OPENAI;
    private String apiKey;
    private String baseUrl;
    private String model;
    private Integer dimensions;

    public EmbeddingProviderType getProvider() {
        return provider;
    }

    public void setProvider(EmbeddingProviderType provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public void setDimensions(Integer dimensions) {
        this.dimensions = dimensions;
    }
}
