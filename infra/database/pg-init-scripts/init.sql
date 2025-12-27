-- ====================================================================
-- VectoPath Database Initialization Script (Test Environment)
-- ====================================================================

-- Drop existing tables in correct order (respecting FK constraints)
DROP TABLE IF EXISTS vector_store CASCADE;
DROP TABLE IF EXISTS resources CASCADE;

-- Drop indexes that might exist
DROP INDEX IF EXISTS vector_store_embedding_idx;
DROP INDEX IF EXISTS resources_name_idx;
DROP INDEX IF EXISTS resources_status_idx;
DROP INDEX IF EXISTS resources_created_at_idx;

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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
                           allowed_roles json,
                           created_at TIMESTAMPTZ DEFAULT now(),
                           updated_at TIMESTAMPTZ DEFAULT now()
);

-- Indexes to optimize queries on resources table
CREATE INDEX resources_name_idx ON resources(name);
CREATE INDEX resources_status_idx ON resources(status);
CREATE INDEX resources_created_at_idx ON resources(created_at DESC);

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
