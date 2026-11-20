-- V1: extensions, archive, contributors, permissions
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE archive (
    id          uuid PRIMARY KEY,
    name        text NOT NULL,
    description text,
    status      text NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE','ARCHIVED')),
    created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE contributor (
    id           uuid PRIMARY KEY,
    archive_id   uuid NOT NULL REFERENCES archive(id),
    display_name text NOT NULL,
    email        text NOT NULL,
    status       text NOT NULL DEFAULT 'ACTIVE'
                 CHECK (status IN ('ACTIVE','SUSPENDED','DEACTIVATED')),
    joined_at    timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_contributor_email ON contributor (archive_id, lower(email));

CREATE TABLE permission (
    id             uuid PRIMARY KEY,
    contributor_id uuid NOT NULL REFERENCES contributor(id),
    scope_type     text NOT NULL CHECK (scope_type IN ('ARCHIVE','FAMILY','PERSON')),
    scope_id       uuid NOT NULL,
    role           text NOT NULL CHECK (role IN ('VIEWER','CONTRIBUTOR','REVIEWER','ADMIN'))
);
CREATE UNIQUE INDEX ux_permission_scope
    ON permission (contributor_id, scope_type, scope_id);

-- The single archive this deployment serves. Every row references it.
INSERT INTO archive (id, name, description)
VALUES ('00000000-0000-7000-8000-000000000001',
        'Bontsi Family Archive',
        'Default archive created by V1. Rename it from the API once the first contributor exists.');

INSERT INTO contributor (id, archive_id, display_name, email)
VALUES ('00000000-0000-7000-8000-000000000002',
        '00000000-0000-7000-8000-000000000001',
        'System Import',
        'system@decoded.local');
