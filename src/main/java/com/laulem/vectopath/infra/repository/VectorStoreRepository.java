package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.PartialResource;
import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.repository.VectorRepository;
import com.laulem.vectopath.infra.repository.exception.VectorStoreAdditionException;
import com.laulem.vectopath.infra.repository.exception.VectorStoreDeletionException;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository
public class VectorStoreRepository implements VectorRepository {

    private static final Logger logger = LoggerFactory.getLogger(VectorStoreRepository.class);

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;
    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    public VectorStoreRepository(VectorStore vectorStore, JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
    }

    public void addResource(Resource resource) {
        logger.info("Adding resource [{}] to vector store", resource.getName());

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
                    .toList();

            vectorStore.add(taggedDocs);
            logger.info("[{}] Loaded {} documents", resource.getName(), taggedDocs.size());

        } catch (Exception e) {
            throw new VectorStoreAdditionException(resource.getName(), e);
        }
    }

    public List<PartialResource> searchSimilar(String query, int limit, String currentUser, List<String> userAuthorities) {
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

            String sql = """
                    WITH authorized_resources AS (
                        SELECT DISTINCT ON (r.id) r.id, r.name, r.content_type, r.metadata, r.created_at, r.updated_at
                        FROM resources r
                        LEFT JOIN resource_allowed_roles rar ON r.id = rar.resource_id AND r.access_level = 'ROLE_LIST'
                        LEFT JOIN app_roles ar ON rar.role_id = ar.id
                        WHERE r.access_level = 'PUBLIC'
                           OR (r.access_level = 'PRIVATE' AND r.created_by = ?)
                           OR (r.access_level = 'ROLE_LIST' AND ar.role_name = ANY(?))
                        ORDER BY r.id
                    )
                    SELECT
                        v.id as vector_id,
                        v.content,
                        ar.id as resource_id,
                        ar.name as resource_name,
                        ar.content_type,
                        ar.metadata,
                        ar.created_at,
                        ar.updated_at
                    FROM vector_store v
                    INNER JOIN authorized_resources ar ON (v.metadata->>'resource_id')::uuid = ar.id
                    WHERE v.metadata->>'chunk_type' = 'content'
                    ORDER BY v.embedding <=> ?
                    LIMIT ?
                    """;

            return jdbcTemplate.query(
                    conn -> {
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setObject(1, currentUser);
                        ps.setArray(2, conn.createArrayOf("text", userAuthorities != null ? userAuthorities.toArray() : new String[0]));
                        ps.setObject(3, pgVector);
                        ps.setInt(4, limit);
                        return ps;
                    },
                    (rs, rowNum) -> {
                        PartialResource partialResource = new PartialResource();
                        partialResource.setVectorId(UUID.fromString(rs.getString("vector_id")));
                        partialResource.setContent(rs.getString("content"));
                        partialResource.setResourceId(UUID.fromString(rs.getString("resource_id")));
                        partialResource.setResourceName(rs.getString("resource_name"));
                        partialResource.setContentType(rs.getString("content_type"));
                        partialResource.setMetadata(rs.getString("metadata"));
                        partialResource.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        partialResource.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                        return partialResource;
                    }
            );

        } catch (Exception e) {
            logger.error("Error during semantic search", e);
            return List.of();
        }
    }

    public void deleteResource(UUID resourceId) {
        try {
            String deleteSql = "DELETE FROM vector_store WHERE metadata->>'resource_id' = ?";
            int deletedCount = jdbcTemplate.update(deleteSql, resourceId.toString());

            if (deletedCount > 0) {
                logger.info("Deleted {} documents for resource {}", deletedCount, resourceId);
            } else {
                logger.warn("No documents found to delete for resource {}", resourceId);
            }
        } catch (Exception e) {
            throw new VectorStoreDeletionException(resourceId, e);
        }
    }

    public boolean isResourceAlreadyLoaded(UUID resourceId) {
        try {
            String sql = "SELECT COUNT(*) FROM vector_store WHERE metadata->>'resource_id' = ? AND metadata->>'chunk_type' = 'marker'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, resourceId.toString());
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error during verification: {}", e.getMessage());
            return false;
        }
    }
}
