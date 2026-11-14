-- V5: parentage, unions, care relationships
CREATE TABLE parentage (
    id               uuid PRIMARY KEY,
    child_id         uuid NOT NULL REFERENCES person(id),
    parent_id        uuid REFERENCES person(id),
    parentage_type   text NOT NULL
                     CHECK (parentage_type IN ('BIOLOGICAL','ADOPTIVE','LEGAL','OTHER_RECOGNISED')),
    identity_status  text NOT NULL
                     CHECK (identity_status IN ('IDENTIFIED','UNIDENTIFIED','UNRECORDED','UNKNOWN')),
    parent_role      text NOT NULL DEFAULT 'UNKNOWN'
                     CHECK (parent_role IN ('FATHER','MOTHER','PARENT','UNKNOWN')),
    reported_name    text,
    valid_from       date,
    valid_to         date,
    asserted_by      uuid NOT NULL REFERENCES contributor(id),
    source_id        uuid REFERENCES source(id),
    status           text NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE','SUPERSEDED','ENTERED_IN_ERROR')),
    superseded_by_id uuid REFERENCES parentage(id),
    created_at       timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_parentage_identity
        CHECK ((identity_status = 'IDENTIFIED') = (parent_id IS NOT NULL)),
    CONSTRAINT ck_parentage_reported_name
        CHECK (reported_name IS NULL OR identity_status = 'UNRECORDED'),
    CONSTRAINT ck_parentage_not_self
        CHECK (parent_id IS NULL OR parent_id <> child_id),
    CONSTRAINT ck_parentage_period
        CHECK (valid_to IS NULL OR valid_from IS NULL OR valid_to > valid_from),
    CONSTRAINT ck_parentage_superseded
        CHECK ((status = 'SUPERSEDED') = (superseded_by_id IS NOT NULL))
);
CREATE UNIQUE INDEX ux_parentage_biological_pair
    ON parentage (child_id, parent_id)
    WHERE parentage_type = 'BIOLOGICAL' AND status = 'ACTIVE' AND parent_id IS NOT NULL;
CREATE INDEX ix_parentage_child  ON parentage (child_id, status);
CREATE INDEX ix_parentage_parent ON parentage (parent_id, status);

-- "union" is a reserved word in SQL, so the table is family_union.
CREATE TABLE family_union (
    id          uuid PRIMARY KEY,
    archive_id  uuid NOT NULL REFERENCES archive(id),
    union_type  text NOT NULL
                CHECK (union_type IN (
                  'MARRIAGE','CIVIL_UNION','CUSTOMARY','COHABITATION','PARTNERSHIP','UNKNOWN')),
    start_date  date,
    end_date    date,
    status      text NOT NULL DEFAULT 'ACTIVE'
                CHECK (status IN ('ACTIVE','ENDED','ANNULLED','ENTERED_IN_ERROR')),
    asserted_by uuid NOT NULL REFERENCES contributor(id),
    source_id   uuid REFERENCES source(id),
    created_at  timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_union_period
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE TABLE family_union_participant (
    union_id         uuid NOT NULL REFERENCES family_union(id),
    person_id        uuid NOT NULL REFERENCES person(id),
    participant_role text NOT NULL CHECK (participant_role IN ('SPOUSE','PARTNER')),
    PRIMARY KEY (union_id, person_id)
);
CREATE INDEX ix_union_participant_person ON family_union_participant (person_id);

CREATE TABLE care_relationship (
    id           uuid PRIMARY KEY,
    caregiver_id uuid NOT NULL REFERENCES person(id),
    recipient_id uuid NOT NULL REFERENCES person(id),
    care_type    text NOT NULL
                 CHECK (care_type IN ('STEP_PARENT','GUARDIAN','FOSTER_PARENT','CAREGIVER','OTHER')),
    start_date   date,
    end_date     date,
    asserted_by  uuid NOT NULL REFERENCES contributor(id),
    source_id    uuid REFERENCES source(id),
    status       text NOT NULL DEFAULT 'ACTIVE'
                 CHECK (status IN ('ACTIVE','ENDED','ENTERED_IN_ERROR')),
    created_at   timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_care_not_self CHECK (caregiver_id <> recipient_id),
    CONSTRAINT ck_care_period
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);
CREATE INDEX ix_care_relationship_recipient ON care_relationship (recipient_id, status);
CREATE INDEX ix_care_relationship_caregiver ON care_relationship (caregiver_id, status);
