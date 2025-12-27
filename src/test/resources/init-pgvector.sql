-- ====================================================================
-- VectoPath Database Initialization Script (Test Environment)
-- ====================================================================

-- Drop existing tables in correct order (respecting FK constraints)
DROP TABLE IF EXISTS resource_allowed_roles CASCADE;
DROP TABLE IF EXISTS vector_store CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS app_roles CASCADE;

-- Drop indexes that might exist
DROP INDEX IF EXISTS vector_store_embedding_idx;
DROP INDEX IF EXISTS resources_name_idx;
DROP INDEX IF EXISTS resources_status_idx;
DROP INDEX IF EXISTS resources_created_at_idx;
DROP INDEX IF EXISTS resource_allowed_roles_resource_idx;
DROP INDEX IF EXISTS resource_allowed_roles_role_idx;

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ====================================================================
-- Table app_roles: Store available roles
-- ====================================================================
CREATE TABLE app_roles (
                           id SERIAL PRIMARY KEY,
                           role_name varchar(100) NOT NULL UNIQUE,
                           description varchar(500),
                           created_at TIMESTAMPTZ DEFAULT now()
);

-- ====================================================================
-- Table resources: Store resource metadata
-- ====================================================================
CREATE TABLE resources (
                           id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
                           name varchar(255) NOT NULL,
                           content text NOT NULL,
                           content_type varchar(100),
                           status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'VECTORIZED', 'ERROR')),
                           metadata json,
                           source_type varchar(20) CHECK (source_type IN ('TEXT', 'URL', 'FILE')),
                           source_name varchar(500),
                           created_by varchar(255),
                           access_level varchar(20) NOT NULL DEFAULT 'PRIVATE' CHECK (access_level IN ('PUBLIC', 'PRIVATE', 'ROLE_LIST')),
                           created_at TIMESTAMPTZ DEFAULT now(),
                           updated_at TIMESTAMPTZ DEFAULT now()
);

-- Indexes to optimize queries on resources table
CREATE INDEX resources_name_idx ON resources(name);
CREATE INDEX resources_status_idx ON resources(status);
CREATE INDEX resources_created_at_idx ON resources(created_at DESC);

-- ====================================================================
-- Table resource_allowed_roles: Many-to-many relationship between resources and roles
-- ====================================================================
CREATE TABLE resource_allowed_roles (
                                        resource_id uuid NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
                                        role_id integer NOT NULL REFERENCES app_roles(id) ON DELETE CASCADE,
                                        created_at TIMESTAMPTZ DEFAULT now(),
                                        PRIMARY KEY (resource_id, role_id)
);

-- Index pour optimiser les recherches par resource_id
CREATE INDEX resource_allowed_roles_resource_idx ON resource_allowed_roles(resource_id);

-- Index pour optimiser les recherches par role_id
CREATE INDEX resource_allowed_roles_role_idx ON resource_allowed_roles(role_id);

-- ====================================================================
-- Table vector_store: Required by Spring AI Vector Store
-- ====================================================================
CREATE TABLE IF NOT EXISTS vector_store (
                                            id text PRIMARY KEY,
                                            content text NOT NULL,
                                            metadata jsonb,
                                            embedding vector(1536)
    );

-- HNSW index for optimized vector searches
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx ON vector_store
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);
