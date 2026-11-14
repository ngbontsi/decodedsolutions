-- V3: identity
CREATE TABLE person (
    id                   uuid PRIMARY KEY,
    archive_id           uuid NOT NULL REFERENCES archive(id),
    gender               text NOT NULL DEFAULT 'UNKNOWN'
                         CHECK (gender IN ('MALE','FEMALE','OTHER','UNKNOWN')),
    birth_date           date,
    birth_date_precision text NOT NULL DEFAULT 'UNKNOWN'
                         CHECK (birth_date_precision IN
                            ('DAY','MONTH','YEAR','DECADE','APPROXIMATE','UNKNOWN')),
    death_date           date,
    death_date_precision text NOT NULL DEFAULT 'UNKNOWN'
                         CHECK (death_date_precision IN
                            ('DAY','MONTH','YEAR','DECADE','APPROXIMATE','UNKNOWN')),
    biography            text,
    is_living            boolean NOT NULL DEFAULT true,
    visibility           text NOT NULL DEFAULT 'FAMILY'
                         CHECK (visibility IN ('PUBLIC','FAMILY','RESTRICTED','PRIVATE')),
    status               text NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','MERGED','ENTERED_IN_ERROR')),
    merged_into_id       uuid REFERENCES person(id),
    created_at           timestamptz NOT NULL DEFAULT now(),
    updated_at           timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_person_dates_ordered
        CHECK (death_date IS NULL OR birth_date IS NULL OR death_date >= birth_date),
    CONSTRAINT ck_person_merged_consistent
        CHECK ((status = 'MERGED') = (merged_into_id IS NOT NULL)),
    CONSTRAINT ck_person_living_consistent
        CHECK (is_living = false OR death_date IS NULL),
    CONSTRAINT ck_person_not_self_merge
        CHECK (merged_into_id IS NULL OR merged_into_id <> id)
);
CREATE INDEX ix_person_archive_status ON person (archive_id, status);
CREATE INDEX ix_person_birth_date     ON person (birth_date);
CREATE INDEX ix_person_merged_into    ON person (merged_into_id);

CREATE TABLE person_name (
    id           uuid PRIMARY KEY,
    person_id    uuid NOT NULL REFERENCES person(id),
    name_type    text NOT NULL
                 CHECK (name_type IN (
                    'BIRTH','MAIDEN','MARRIED','CLAN','NICKNAME','ALIAS','RELIGIOUS','OTHER')),
    given_names  text,
    family_name  text,
    full_name    text NOT NULL,
    script       text NOT NULL DEFAULT 'LATIN',
    language     text,
    is_preferred boolean NOT NULL DEFAULT false,
    valid_from   date,
    valid_to     date,
    source_id    uuid REFERENCES source(id),
    created_at   timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_person_name_not_blank CHECK (length(btrim(full_name)) > 0),
    CONSTRAINT ck_person_name_period
        CHECK (valid_to IS NULL OR valid_from IS NULL OR valid_to > valid_from)
);
CREATE UNIQUE INDEX ux_person_name_preferred
    ON person_name (person_id) WHERE is_preferred;
CREATE UNIQUE INDEX ux_person_name_pair
    ON person_name (person_id, name_type, full_name, COALESCE(valid_from, DATE '0001-01-01'));
CREATE INDEX ix_person_name_person ON person_name (person_id);
CREATE INDEX ix_person_name_search ON person_name USING gin (full_name gin_trgm_ops);
