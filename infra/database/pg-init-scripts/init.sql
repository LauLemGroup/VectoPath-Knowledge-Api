-- ====================================================================
-- Script d'initialisation VectoPath Database
-- ====================================================================

-- Suppression des tables existantes (dans l'ordre correct pour respecter les FK)
DROP TABLE IF EXISTS vector_store CASCADE;
DROP TABLE IF EXISTS resources CASCADE;

-- Suppression des index qui pourraient exister
DROP INDEX IF EXISTS vector_store_embedding_idx;
DROP INDEX IF EXISTS resources_name_idx;
DROP INDEX IF EXISTS resources_status_idx;
DROP INDEX IF EXISTS resources_created_at_idx;

-- Activation des extensions nécessaires
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ====================================================================
-- Table resources : Stockage des métadonnées des ressources
-- ====================================================================
CREATE TABLE resources (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    name varchar(255) NOT NULL,
    content text NOT NULL,
    content_type varchar(100),
    status varchar(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'VECTORIZED', 'ERROR')),
    metadata json,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Index pour optimiser les requêtes sur la table resources
CREATE INDEX resources_name_idx ON resources(name);
CREATE INDEX resources_status_idx ON resources(status);
CREATE INDEX resources_created_at_idx ON resources(created_at DESC);



-- ====================================================================
-- Table vector_store : Requise par Spring AI Vector Store
-- ====================================================================
CREATE TABLE IF NOT EXISTS vector_store (
    id text PRIMARY KEY,
    content text NOT NULL,
    metadata jsonb,
    embedding vector(1536)
);

-- Index HNSW pour les recherches vectorielles optimisées
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx ON vector_store
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

