-- Migration pour ajouter les champs source_type, source_name, created_by, access_level et allowed_roles
-- À exécuter sur une base de données existante

-- Ajouter la colonne source_type
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS source_type varchar(20) CHECK (source_type IN ('TEXT', 'URL', 'FILE'));

-- Ajouter la colonne source_name
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS source_name varchar(500);

-- Ajouter la colonne created_by
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS created_by varchar(255);

-- Ajouter la colonne access_level
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS access_level varchar(20) NOT NULL DEFAULT 'PRIVATE' CHECK (access_level IN ('PUBLIC', 'PRIVATE', 'ROLE_LIST'));

-- Ajouter la colonne allowed_roles
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS allowed_roles json;
