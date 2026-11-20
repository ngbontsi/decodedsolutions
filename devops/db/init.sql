-- devops/db/init.sql
-- Local development database initialization
-- Mirrors production Neon setup: per-service roles, databases, and RLS policies

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create roles (matching Neon role names: {service}_app)
DO $$
DECLARE
    svc text;
BEGIN
    FOREACH svc IN ARRAY ARRAY['auth', 'restaurant', 'guesthouse', 'marketplace', 'familytree']
    LOOP
        EXECUTE format('CREATE ROLE %I_app WITH LOGIN PASSWORD ''changeme'';', svc)
        EXCEPTION WHEN duplicate_object THEN
            RAISE NOTICE 'Role %_app already exists', svc;
    END LOOP;
END $$;

-- Create databases owned by respective roles
DO $$
DECLARE
    svc text;
    dbname text;
BEGIN
    FOREACH svc IN ARRAY ARRAY['auth', 'restaurant', 'guesthouse', 'marketplace', 'familytree']
    LOOP
        dbname := svc || 'db';
        EXECUTE format('CREATE DATABASE %I OWNER %I_app;', dbname, svc)
        EXCEPTION WHEN duplicate_database THEN
            RAISE NOTICE 'Database % already exists', dbname;
    END LOOP;
END $$;

-- Grant usage on public schema to all app roles
DO $$
DECLARE
    svc text;
BEGIN
    FOREACH svc IN ARRAY ARRAY['auth', 'restaurant', 'guesthouse', 'marketplace', 'familytree']
    LOOP
        EXECUTE format('GRANT USAGE ON SCHEMA public TO %I_app;', svc);
        EXECUTE format('GRANT CREATE ON SCHEMA public TO %I_app;', svc);
    END LOOP;
END $$;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO auth_app, restaurant_app, guesthouse_app, marketplace_app, familytree_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO auth_app, restaurant_app, guesthouse_app, marketplace_app, familytree_app;

-- Note: RLS policies are applied by Flyway migrations in each service
-- See devops/db/rls/setup_rls.sql for production RLS policies
-- For local dev, run: psql -U platformuser -d <dbname> -f devops/db/rls/setup_rls.sql