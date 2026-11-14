-- V2: places and sources
CREATE TABLE location (
    id                 uuid PRIMARY KEY,
    archive_id         uuid NOT NULL REFERENCES archive(id),
    name               text NOT NULL,
    location_type      text NOT NULL
                       CHECK (location_type IN ('COUNTRY','PROVINCE','TOWN','SITE','ADDRESS','OTHER')),
    parent_location_id uuid REFERENCES location(id),
    latitude           numeric(9,6) CHECK (latitude BETWEEN -90 AND 90),
    longitude          numeric(9,6) CHECK (longitude BETWEEN -180 AND 180),
    description        text,
    CONSTRAINT ck_location_not_self
        CHECK (parent_location_id IS NULL OR parent_location_id <> id)
);
CREATE INDEX ix_location_parent ON location (parent_location_id);

CREATE TABLE source (
    id                uuid PRIMARY KEY,
    archive_id        uuid NOT NULL REFERENCES archive(id),
    source_type       text NOT NULL
                      CHECK (source_type IN (
                        'FAMILY_TESTIMONY','DOCUMENT','PHOTOGRAPH','AUDIO','VIDEO',
                        'PUBLICATION','ARCHIVE_RECORD','GOVERNMENT_RECORD','ORAL_HISTORY','OTHER')),
    title             text NOT NULL,
    author_or_speaker text,
    institution       text,
    reference_code    text,
    recorded_date     date,
    location_id       uuid REFERENCES location(id),
    access_level      text NOT NULL DEFAULT 'FAMILY'
                      CHECK (access_level IN ('PUBLIC','FAMILY','RESTRICTED','PRIVATE')),
    notes             text,
    created_at        timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_source_location ON source (location_id);

CREATE TABLE document (
    id                uuid PRIMARY KEY,
    source_id         uuid NOT NULL REFERENCES source(id),
    media_type        text NOT NULL,
    storage_key       text,
    original_filename text,
    checksum_sha256   text,
    captured_date     date,
    description       text,
    created_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_document_checksum_len
        CHECK (checksum_sha256 IS NULL OR length(checksum_sha256) = 64),
    CONSTRAINT ck_document_file_has_checksum
        CHECK (storage_key IS NULL OR checksum_sha256 IS NOT NULL)
);
CREATE UNIQUE INDEX ux_document_storage_key
    ON document (storage_key) WHERE storage_key IS NOT NULL;
CREATE INDEX ix_document_source ON document (source_id);
