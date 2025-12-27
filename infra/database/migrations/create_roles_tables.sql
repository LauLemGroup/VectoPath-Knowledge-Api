-- Migration pour créer les tables de gestion des rôles
-- À exécuter sur une base de données existante

-- ====================================================================
-- Table app_roles: Store available roles
-- ====================================================================
CREATE TABLE IF NOT EXISTS app_roles (
    id SERIAL PRIMARY KEY,
    role_name varchar(100) NOT NULL UNIQUE,
    description varchar(500),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Insérer le rôle par défaut
INSERT INTO app_roles (role_name, description)
VALUES ('DEFAULT', 'Default role for all authenticated users')
ON CONFLICT (role_name) DO NOTHING;

-- ====================================================================
-- Table resource_allowed_roles: Many-to-many relationship between resources and roles
-- ====================================================================
CREATE TABLE IF NOT EXISTS resource_allowed_roles (
    resource_id uuid NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
    role_id integer NOT NULL REFERENCES app_roles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (resource_id, role_id)
);

-- Index pour optimiser les recherches par resource_id
CREATE INDEX IF NOT EXISTS resource_allowed_roles_resource_idx ON resource_allowed_roles(resource_id);

-- Index pour optimiser les recherches par role_id
CREATE INDEX IF NOT EXISTS resource_allowed_roles_role_idx ON resource_allowed_roles(role_id);

-- ====================================================================
-- Migration des données existantes
-- ====================================================================
-- Pour chaque ressource avec allowed_roles non null, créer les entrées dans resource_allowed_roles
DO $$
DECLARE
    resource_record RECORD;
    role_text TEXT;
    role_record RECORD;
BEGIN
    -- Parcourir toutes les ressources ayant des rôles dans allowed_roles
    FOR resource_record IN
        SELECT id, allowed_roles
        FROM resources
        WHERE allowed_roles IS NOT NULL
        AND allowed_roles::text != '[]'
        AND allowed_roles::text != 'null'
    LOOP
        -- Parcourir chaque rôle dans le JSON array
        FOR role_text IN
            SELECT jsonb_array_elements_text(resource_record.allowed_roles::jsonb)
        LOOP
            -- Insérer ou récupérer le rôle dans app_roles
            INSERT INTO app_roles (role_name, description)
            VALUES (role_text, 'Migrated from allowed_roles column')
            ON CONFLICT (role_name) DO NOTHING;

            -- Récupérer l'ID du rôle
            SELECT id INTO role_record FROM app_roles WHERE role_name = role_text;

            -- Créer la relation dans resource_allowed_roles
            INSERT INTO resource_allowed_roles (resource_id, role_id)
            VALUES (resource_record.id, role_record.id)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- ====================================================================
-- Supprimer l'ancienne colonne allowed_roles
-- ====================================================================
ALTER TABLE resources DROP COLUMN IF EXISTS allowed_roles;

