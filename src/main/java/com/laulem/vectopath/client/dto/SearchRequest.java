package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class SearchRequest {

    @JsonProperty("query")
    private String query;

    @JsonProperty(value = "limit", defaultValue = "10")
    private int limit = 10;

    @JsonProperty(value = "min_similarity", defaultValue = "0.50")
    private double minSimilarity = 0.5;

    @JsonProperty("resource_ids")
    private List<UUID> resourceIds;

    public SearchRequest() {
    }

    public SearchRequest(String query, Integer limit, Double minSimilarity) {
        this.query = query;
        if (limit != null) {
            this.limit = limit;
        }

        if (minSimilarity != null) {
            this.minSimilarity = minSimilarity;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public double getMinSimilarity() {
        return minSimilarity;
    }

    public void setMinSimilarity(double minSimilarity) {
        this.minSimilarity = minSimilarity;
    }

    public List<UUID> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<UUID> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
