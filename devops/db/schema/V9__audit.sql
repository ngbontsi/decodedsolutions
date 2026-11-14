-- V9: audit trail (append-only)
CREATE TABLE audit_log (
    id                   uuid PRIMARY KEY,
    archive_id           uuid NOT NULL REFERENCES archive(id),
    actor_contributor_id uuid REFERENCES contributor(id),
    entity_type          text NOT NULL,
    entity_id            uuid NOT NULL,
    action               text NOT NULL
                         CHECK (action IN (
                           'CREATE','UPDATE','SUPERSEDE','MERGE','STATUS_CHANGE','WITHDRAW')),
    before_state         jsonb,
    after_state          jsonb,
    reason               text,
    occurred_at          timestamptz NOT NULL DEFAULT now(),
    request_id           text
);
CREATE INDEX ix_audit_entity ON audit_log (entity_type, entity_id, occurred_at DESC);
CREATE INDEX ix_audit_actor  ON audit_log (actor_contributor_id, occurred_at DESC);

CREATE OR REPLACE FUNCTION deny_audit_mutation() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_log is append-only';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_log_immutable
    BEFORE UPDATE OR DELETE ON audit_log
    FOR EACH ROW EXECUTE FUNCTION deny_audit_mutation();
