package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.repository.ResourceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgreSQLResourceRepository implements ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostgreSQLResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Resource save(Resource resource) {
        String sql = "INSERT INTO resources (id, name, content, content_type, status, metadata, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?::json, ?, ?)";

        jdbcTemplate.update(sql,
                resource.getId(),
                resource.getName(),
                resource.getContent(),
                resource.getContentType(),
                resource.getStatus().name(),
                resource.getMetadata(),
                Timestamp.valueOf(resource.getCreatedAt()),
                Timestamp.valueOf(resource.getUpdatedAt())
        );

        return resource;
    }

    @Override
    public Optional<Resource> findById(UUID id) {
        String sql = "SELECT * FROM resources WHERE id = ?";
        List<Resource> results = jdbcTemplate.query(sql, new ResourceRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Resource> findAll() {
        String sql = "SELECT * FROM resources ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ResourceRowMapper());
    }

    @Override
    public List<Resource> findByStatus(ResourceStatus status) {
        String sql = "SELECT * FROM resources WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ResourceRowMapper(), status.name());
    }

    @Override
    public List<Resource> findByNameContaining(String name) {
        String sql = "SELECT * FROM resources WHERE name ILIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new ResourceRowMapper(), "%" + name + "%");
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM resources WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM resources WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private Resource insert(Resource resource) {
        String sql = "INSERT INTO resources (id, name, content, content_type, status, metadata, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?::json, ?, ?)";

        jdbcTemplate.update(sql,
                resource.getId(),
                resource.getName(),
                resource.getContent(),
                resource.getContentType(),
                resource.getStatus().name(),
                resource.getMetadata(),
                Timestamp.valueOf(resource.getCreatedAt()),
                Timestamp.valueOf(resource.getUpdatedAt())
        );

        return resource;
    }

    private Resource update(Resource resource) {
        String sql = "UPDATE resources SET name = ?, content = ?, content_type = ?, status = ?, " +
                "metadata = ?::json, updated_at = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                resource.getName(),
                resource.getContent(),
                resource.getContentType(),
                resource.getStatus().name(),
                resource.getMetadata(),
                Timestamp.valueOf(resource.getUpdatedAt()),
                resource.getId()
        );

        return resource;
    }

    private static class ResourceRowMapper implements RowMapper<Resource> {
        @Override
        public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
            Resource resource = new Resource();
            resource.setId((UUID) rs.getObject("id"));
            resource.setName(rs.getString("name"));
            resource.setContent(rs.getString("content"));
            resource.setContentType(rs.getString("content_type"));
            resource.setStatus(ResourceStatus.valueOf(rs.getString("status")));
            resource.setMetadata(rs.getString("metadata"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                resource.setCreatedAt(createdAt.toLocalDateTime());
            }

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                resource.setUpdatedAt(updatedAt.toLocalDateTime());
            }

            return resource;
        }
    }
}
