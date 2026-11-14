-- =============================================================================
-- Multi-Store Data Foundation — migration mirror for Decoded Solutions Platform
-- Reference for Phase A (data foundation) of the multi-store/multi-branch rework.
-- Live schema is applied by the services themselves via `ddl-auto: update`
-- (restaurant, guesthouse, marketplace). This script is the source-of-truth
-- backup for fresh database setup and manual verification against running DBs.
-- Idempotent: safe to run repeatedly (CREATE TABLE IF NOT EXISTS / ADD COLUMN
-- IF NOT EXISTS).
-- Design reference: business/docs/developer-docs/MULTI_STORE_DATA_AND_API_DESIGN.md
-- =============================================================================

-- ==================== restaurantdb (restaurant-service) ====================
\c restaurantdb;

CREATE TABLE IF NOT EXISTS businesses (
    id          VARCHAR(255) PRIMARY KEY,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    logo_url    VARCHAR(255),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stores (
    id          VARCHAR(255) PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(255),
    phone       VARCHAR(100),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

ALTER TABLE menu_items ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);

-- ==================== guesthousedb (guesthouse-service) ====================
\c guesthousedb;

CREATE TABLE IF NOT EXISTS businesses (
    id          VARCHAR(255) PRIMARY KEY,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    logo_url    VARCHAR(255),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stores (
    id          VARCHAR(255) PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(255),
    phone       VARCHAR(100),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

ALTER TABLE rooms         ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);
ALTER TABLE reservations  ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);

-- ==================== marketplacedb (marketplace-service) ====================
\c marketplacedb;

CREATE TABLE IF NOT EXISTS businesses (
    id          VARCHAR(255) PRIMARY KEY,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    logo_url    VARCHAR(255),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stores (
    id          VARCHAR(255) PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    owner_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(255),
    phone       VARCHAR(100),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

ALTER TABLE products    ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);
ALTER TABLE categories  ADD COLUMN IF NOT EXISTS business_id VARCHAR(255);
ALTER TABLE carts       ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);
ALTER TABLE orders      ADD COLUMN IF NOT EXISTS store_id VARCHAR(255);