-- V4: family groups and membership
CREATE TABLE family_group (
    id           uuid PRIMARY KEY,
    archive_id   uuid NOT NULL REFERENCES archive(id),
    name         text NOT NULL,
    description  text,
    lineage_type text NOT NULL DEFAULT 'UNKNOWN'
                 CHECK (lineage_type IN ('PATRILINEAL','MATRILINEAL','BILATERAL','UNKNOWN')),
    status       text NOT NULL DEFAULT 'ACTIVE'
                 CHECK (status IN ('ACTIVE','ENTERED_IN_ERROR')),
    created_at   timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX ux_family_group_name ON family_group (archive_id, lower(name));

CREATE TABLE family_membership (
    id              uuid PRIMARY KEY,
    person_id       uuid NOT NULL REFERENCES person(id),
    family_group_id uuid NOT NULL REFERENCES family_group(id),
    connection_type text NOT NULL
                    CHECK (connection_type IN ('MEMBER','HEAD','ELDER','DESCENDANT','AFFILIATE')),
    basis           text NOT NULL
                    CHECK (basis IN (
                      'PARENTAGE','BIRTH','MARRIAGE','ADOPTION','CULTURAL',
                      'SOCIAL','LEGAL','SELF_IDENTIFIED','OTHER')),
    valid_from      date,
    valid_to        date,
    asserted_by     uuid NOT NULL REFERENCES contributor(id),
    source_id       uuid REFERENCES source(id),
    status          text NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE','WITHDRAWN','ENTERED_IN_ERROR')),
    created_at      timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_family_membership_period
        CHECK (valid_to IS NULL OR valid_from IS NULL OR valid_to > valid_from)
);
CREATE UNIQUE INDEX ux_family_membership_active
    ON family_membership (person_id, family_group_id, connection_type)
    WHERE status = 'ACTIVE' AND valid_to IS NULL;
CREATE INDEX ix_family_membership_person ON family_membership (person_id);
CREATE INDEX ix_family_membership_group  ON family_membership (family_group_id);
