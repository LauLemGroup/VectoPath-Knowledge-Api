package com.laulem.vectopath.infra.repository;

import com.laulem.vectopath.business.model.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, UUID> {

    List<ResourceEntity> findByStatus(ResourceStatus status);
    List<ResourceEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Requête SQL native avec filtrage d'accès conditionnel :
     * - PUBLIC : accessible par tous
     * - PRIVATE : accessible uniquement par le créateur
     * - ROLE_LIST : accessible si l'utilisateur a au moins un rôle autorisé
     */
    @Query(value = """
        SELECT * FROM resources r 
        WHERE (:id IS NULL OR r.id = CAST(:id AS uuid))
        AND (:status IS NULL OR r.status = :status)
        AND (:searchName IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :searchName, '%')))
        AND (
            r.access_level = 'PUBLIC'
            OR (r.access_level = 'PRIVATE' AND r.created_by = :username)
            OR (r.access_level = 'ROLE_LIST' 
                AND r.allowed_roles IS NOT NULL 
                AND EXISTS (
                    SELECT 1 FROM jsonb_array_elements_text(r.allowed_roles) AS role
                    WHERE role = ANY(CAST(:userRoles AS text[]))
                ))
        )
        ORDER BY r.created_at DESC
        """, nativeQuery = true)
    List<ResourceEntity> findWithAccessControl(
        @Param("id") String id,
        @Param("status") String status,
        @Param("searchName") String searchName,
        @Param("username") String username,
        @Param("userRoles") String[] userRoles
    );
}
