package com.laulem.vectopath.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO pour les requêtes de recherche
 */
public class SearchRequest {

    @JsonProperty("query")
    private String query;

    @JsonProperty("limit")
    private int limit = 10;

    public SearchRequest() {}

    public SearchRequest(String query, int limit) {
        this.query = query;
        this.limit = limit;
    }

    // Getters et Setters
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
}
