-- Migration to add the fields source_type, source_name, created_by, access_level and allowed_roles
-- To be executed on an existing database

-- Add the column source_type
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS source_type varchar(20) CHECK (source_type IN ('TEXT', 'URL', 'FILE'));

-- Add the column source_name
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS source_name varchar(500);

-- Add the column created_by
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS created_by varchar(255);

-- Add the column access_level
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS access_level varchar(20) NOT NULL DEFAULT 'PRIVATE' CHECK (access_level IN ('PUBLIC', 'PRIVATE', 'ROLE_LIST'));

-- Add the column allowed_roles
ALTER TABLE resources
ADD COLUMN IF NOT EXISTS allowed_roles json;
