package com.laulem.vectopath.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laulem.vectopath.client.dto.SearchRequest;
import com.laulem.vectopath.testconfig.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    private UUID resourceId;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        jdbcTemplate.execute("DELETE FROM vector_store");

        // Create mock embedding vector (1536 dimensions for text-embedding-3-small)
        float[] mockEmbedding = new float[1536];
        for (int i = 0; i < mockEmbedding.length; i++) {
            mockEmbedding[i] = 0.1f + (i * 0.001f);
        }

        // Mock OpenAI call
        when(embeddingModel.embed(anyString())).thenReturn(mockEmbedding);

        // Insert test data into the database
        resourceId = UUID.randomUUID();
        insertTestData(resourceId);
    }

    @Test
    void searchSemantic_shouldReturnResults_fromDatabase() throws Exception {
        // Given
        SearchRequest request = new SearchRequest("java programming", 5);

        // When & Then
        mockMvc.perform(post("/api/v1/search/semantic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].content", notNullValue()))
                .andExpect(jsonPath("$[0].resource_id", is(resourceId.toString())))
                .andExpect(jsonPath("$[0].metadata", notNullValue()));
    }

    @Test
    void searchSemantic_shouldRespectLimit() throws Exception {
        // Given
        SearchRequest request = new SearchRequest("test query", 2);

        // When & Then
        mockMvc.perform(post("/api/v1/search/semantic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(2))));
    }

    @Test
    void searchSemantic_shouldReturnEmptyList_whenNoDataInDatabase() throws Exception {
        // Given
        jdbcTemplate.execute("DELETE FROM vector_store");
        SearchRequest request = new SearchRequest("nonexistent query", 10);

        // When & Then
        mockMvc.perform(post("/api/v1/search/semantic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchSemantic_shouldUseDefaultLimit() throws Exception {
        // Given
        for (int i = 0; i < 15; i++) {
            UUID id = UUID.randomUUID();
            insertTestData(id);
        }

        String requestJson = "{\"query\":\"test query\"}";

        // When & Then
        mockMvc.perform(post("/api/v1/search/semantic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lessThanOrEqualTo(10))));
    }

    private void insertTestData(UUID resourceId) {
        // Create embedding vector for PostgreSQL
        StringBuilder vectorStr = new StringBuilder("[");
        for (int i = 0; i < 1536; i++) {
            if (i > 0) vectorStr.append(",");
            vectorStr.append(0.1f + (i * 0.001f));
        }
        vectorStr.append("]");

        String sql = """
                INSERT INTO vector_store (id, content, metadata, embedding)
                VALUES (?, ?, ?::jsonb, ?::vector)
                """;

        String content1 = "Java is a popular programming language used for building enterprise applications.";
        String metadata1 = String.format(
                "{\"resource_id\":\"%s\",\"resource_name\":\"test.txt\",\"content_type\":\"text/plain\",\"status\":\"VECTORIZED\",\"chunk_type\":\"content\"}",
                resourceId
        );

        String content2 = "Spring Boot simplifies the development of Spring applications with auto-configuration.";
        String metadata2 = String.format(
                "{\"resource_id\":\"%s\",\"resource_name\":\"test.txt\",\"content_type\":\"text/plain\",\"status\":\"VECTORIZED\",\"chunk_type\":\"content\"}",
                resourceId
        );

        String content3 = "PostgreSQL with pgvector extension enables vector similarity search.";
        String metadata3 = String.format(
                "{\"resource_id\":\"%s\",\"resource_name\":\"test.txt\",\"content_type\":\"text/plain\",\"status\":\"VECTORIZED\",\"chunk_type\":\"content\"}",
                resourceId
        );

        jdbcTemplate.update(sql, UUID.randomUUID().toString(), content1, metadata1, vectorStr.toString());
        jdbcTemplate.update(sql, UUID.randomUUID().toString(), content1, metadata1, vectorStr.toString());
        jdbcTemplate.update(sql, UUID.randomUUID().toString(), content2, metadata2, vectorStr.toString());
        jdbcTemplate.update(sql, UUID.randomUUID().toString(), content3, metadata3, vectorStr.toString());
    }
}
