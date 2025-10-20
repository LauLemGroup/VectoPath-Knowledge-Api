package com.laulem.vectopath.infra.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.DocumentChunk;
import com.laulem.vectopath.business.repository.VectorRepository;
import org.postgresql.util.PGobject;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptateur d'infrastructure implémentant le port VectorRepository
 * Architecture hexagonale respectée
 */
@Repository
public class VectorStoreRepository implements VectorRepository {

    private static final Logger logger = LoggerFactory.getLogger(VectorStoreRepository.class);

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    public VectorStoreRepository(VectorStore vectorStore, JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
    }

    public void addResource(Resource resource) {
        logger.info("Ajout de la ressource [{}] dans le vector store", resource.getName());

        try {
            Document rawDoc = new Document(resource.getContent());
            List<Document> splitDocs = tokenTextSplitter.apply(List.of(rawDoc));

            List<Document> taggedDocs = splitDocs.stream()
                    .map(doc -> new Document(doc.getText(), Map.of(
                            "resource_id", resource.getId().toString(),
                            "resource_name", resource.getName(),
                            "content_type", resource.getContentType(),
                            "status", resource.getStatus().name(),
                            "chunk_type", "content"
                    )))
                    .collect(Collectors.toList());

            vectorStore.add(taggedDocs);
            logger.info("[{}] Chargé {} documents", resource.getName(), taggedDocs.size());

        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout de la ressource [{}]", resource.getName(), e);
            throw new RuntimeException("Erreur lors de l'ajout de la ressource", e);
        }
    }

    public List<DocumentChunk> searchSimilar(String query, int limit) {
        try {
            float[] vector = embeddingModel.embed(query);

            PGobject pgVector = new PGobject();
            pgVector.setType("vector");
            StringBuilder vectorStr = new StringBuilder("[");
            for (int i = 0; i < vector.length; i++) {
                if (i > 0) vectorStr.append(",");
                vectorStr.append(vector[i]);
            }
            pgVector.setValue(vectorStr.append("]").toString());

            String sql = "SELECT id, content, metadata FROM vector_store WHERE metadata->>'chunk_type' = 'content' ORDER BY embedding <=> ? LIMIT ?";

            return jdbcTemplate.query(
                    conn -> {
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setObject(1, pgVector);
                        ps.setInt(2, limit);
                        return ps;
                    },
                    (rs, rowNum) -> convertToDocumentChunk(
                            rs.getString("id"),
                            rs.getString("content"),
                            parseMetadata(rs.getString("metadata"))
                    )
            );

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche sémantique", e);
            return List.of();
        }
    }

    public List<DocumentChunk> getChunksByResourceId(UUID resourceId) {
        String sql = "SELECT id, content, metadata FROM vector_store WHERE metadata->>'resource_id' = ? AND metadata->>'chunk_type' = 'content' ORDER BY id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> convertToDocumentChunk(
                rs.getString("id"),
                rs.getString("content"),
                parseMetadata(rs.getString("metadata"))
        ), resourceId.toString());
    }

    public void deleteResource(UUID resourceId) {
        try {
            String sql = "SELECT id FROM vector_store WHERE metadata->>'resource_id' = ?";
            List<String> documentIds = jdbcTemplate.queryForList(sql, String.class, resourceId.toString());

            if (!documentIds.isEmpty()) {
                vectorStore.delete(documentIds);
                logger.info("Supprimé {} documents pour la ressource {}", documentIds.size(), resourceId);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la ressource : {}", resourceId, e);
        }
    }

    public boolean isResourceAlreadyLoaded(UUID resourceId) {
        try {
            String sql = "SELECT COUNT(*) FROM vector_store WHERE metadata->>'resource_id' = ? AND metadata->>'chunk_type' = 'marker'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, resourceId.toString());
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification : {}", e.getMessage());
            return false;
        }
    }

    private Map<String, Object> parseMetadata(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private DocumentChunk convertToDocumentChunk(String id, String content, Map<String, Object> metadata) {
        DocumentChunk chunk = new DocumentChunk();

        try {
            chunk.setId(UUID.fromString(id));
        } catch (Exception e) {
            chunk.setId(UUID.randomUUID());
        }

        chunk.setResourceId(UUID.fromString(metadata.get("resource_id").toString()));
        chunk.setContent(content);
        chunk.setCreatedAt(LocalDateTime.now());

        chunk.setMetadata(String.format(
                "{\"resourceId\":\"%s\",\"resourceName\":\"%s\",\"contentType\":\"%s\",\"status\":\"%s\"}",
                metadata.get("resource_id"),
                metadata.get("resource_name"),
                metadata.get("content_type"),
                metadata.get("status")
        ));

        return chunk;
    }
}
