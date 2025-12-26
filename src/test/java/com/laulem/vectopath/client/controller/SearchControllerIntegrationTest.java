package com.laulem.vectopath.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laulem.vectopath.client.dto.SearchRequest;
import com.laulem.vectopath.testconfig.TestDataLoader;
import com.laulem.vectopath.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@Transactional
@Import(TestcontainersConfiguration.class)
class SearchControllerIntegrationTest {
    public static final String SEARCH_SEMANTIC_PATH = "/api/v1/search/semantic";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataLoader testDataLoader;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @BeforeEach
    void setUp() {
        testDataLoader.cleanDatabase();
        testDataLoader.loadTestDefaultData();

        when(embeddingModel.embed(anyString())).thenReturn(testDataLoader.createDefaultEmbeddingFloatVector());
    }

    @Test
    void searchSemantic_shouldReturnResults_fromDatabase() throws Exception {
        // Given
        SearchRequest request = new SearchRequest("java programming", 5);

        // When & Then
        mockMvc.perform(post(SEARCH_SEMANTIC_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].vector_id", notNullValue()))
                .andExpect(jsonPath("$[0].content", notNullValue()))
                .andExpect(jsonPath("$[0].resource_id", is("550e8400-e29b-41d4-a716-446655440000")))
                .andExpect(jsonPath("$[0].resource_name", is("test.txt")))
                .andExpect(jsonPath("$[0].content_type", is("text/plain")))
                .andExpect(jsonPath("$[0].metadata", notNullValue()))
                .andExpect(jsonPath("$[0].created_at", notNullValue()))
                .andExpect(jsonPath("$[0].updated_at", notNullValue()));
    }

    @Test
    void searchSemantic_shouldRespectLimit() throws Exception {
        // Given
        SearchRequest request = new SearchRequest("test query", 2);

        // When & Then
        mockMvc.perform(post(SEARCH_SEMANTIC_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(2))));
    }

    @Test
    void searchSemantic_shouldReturnEmptyList_whenNoDataInDatabase() throws Exception {
        // Given
        testDataLoader.cleanDatabase();

        SearchRequest request = new SearchRequest("nonexistent query", 10);

        // When & Then
        mockMvc.perform(post(SEARCH_SEMANTIC_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchSemantic_shouldUseDefaultLimit() throws Exception {
        // Given
        String requestJson = "{\"query\":\"test query\"}";

        // When & Then
        mockMvc.perform(post(SEARCH_SEMANTIC_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }
}
