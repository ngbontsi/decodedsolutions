-- V7: claims and evidence
CREATE TABLE claim (
    id                      uuid PRIMARY KEY,
    archive_id              uuid NOT NULL REFERENCES archive(id),
    subject_person_id       uuid REFERENCES person(id),
    subject_organisation_id uuid REFERENCES organisation(id),
    subject_event_id        uuid REFERENCES historical_event(id),
    statement               text NOT NULL,
    status                  text NOT NULL DEFAULT 'UNVERIFIED'
                            CHECK (status IN (
                              'UNVERIFIED','UNDER_REVIEW','SUPPORTED','DISPUTED','RETRACTED')),
    confidence              text NOT NULL DEFAULT 'LOW'
                            CHECK (confidence IN ('LOW','MEDIUM','HIGH')),
    submitted_by            uuid NOT NULL REFERENCES contributor(id),
    submitted_at            timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_claim_one_subject
        CHECK (num_nonnulls(subject_person_id, subject_organisation_id, subject_event_id) = 1)
);
CREATE INDEX ix_claim_person_status  ON claim (subject_person_id, status);
CREATE INDEX ix_claim_archive_status ON claim (archive_id, status);

CREATE TABLE claim_evidence (
    claim_id    uuid NOT NULL REFERENCES claim(id),
    evidence_id uuid NOT NULL REFERENCES document(id),
    stance      text NOT NULL CHECK (stance IN ('SUPPORTS','CONTRADICTS','CONTEXT')),
    weight      text NOT NULL CHECK (weight IN ('WEAK','MODERATE','STRONG')),
    note        text,
    added_by    uuid NOT NULL REFERENCES contributor(id),
    added_at    timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (claim_id, evidence_id)
);
CREATE INDEX ix_claim_evidence_document ON claim_evidence (evidence_id);
