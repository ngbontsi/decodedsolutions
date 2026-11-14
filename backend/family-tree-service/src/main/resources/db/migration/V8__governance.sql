-- V8: governance
CREATE TABLE invitation (
    id            uuid PRIMARY KEY,
    archive_id    uuid NOT NULL REFERENCES archive(id),
    invited_email text NOT NULL,
    invited_by    uuid NOT NULL REFERENCES contributor(id),
    role          text NOT NULL CHECK (role IN ('VIEWER','CONTRIBUTOR','REVIEWER','ADMIN')),
    token_hash    text NOT NULL CHECK (length(token_hash) = 64),
    expires_at    timestamptz NOT NULL,
    accepted_at   timestamptz,
    created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_invitation_email ON invitation (archive_id, lower(invited_email));

CREATE TABLE consent (
    id           uuid PRIMARY KEY,
    person_id    uuid NOT NULL REFERENCES person(id),
    consent_type text NOT NULL,
    granted_by   uuid NOT NULL REFERENCES contributor(id),
    granted_at   timestamptz NOT NULL DEFAULT now(),
    expires_at   timestamptz,
    document_id  uuid REFERENCES document(id)
);
CREATE INDEX ix_consent_person ON consent (person_id);

CREATE TABLE review (
    id          uuid PRIMARY KEY,
    claim_id    uuid NOT NULL REFERENCES claim(id),
    reviewer_id uuid NOT NULL REFERENCES contributor(id),
    decision    text NOT NULL CHECK (decision IN ('APPROVE','REJECT','REQUEST_CHANGES')),
    comment     text,
    reviewed_at timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_review_claim ON review (claim_id, reviewed_at DESC);
