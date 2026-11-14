package com.platform.familytree.shared.audit;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Immutable
public class AuditLog {

    public enum Action {
        CREATE, UPDATE, SUPERSEDE, MERGE, STATUS_CHANGE, WITHDRAW
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(name = "actor_contributor_id")
    private UUID actorContributorId;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_state")
    private String beforeState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state")
    private String afterState;

    private String reason;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "request_id")
    private String requestId;

    protected AuditLog() {
    }

    public static AuditLog of(UUID archiveId,
                              UUID actorContributorId,
                              String entityType,
                              UUID entityId,
                              Action action,
                              String beforeState,
                              String afterState,
                              String reason) {
        AuditLog entry = new AuditLog();
        entry.id = Uuid7.generate();
        entry.archiveId = archiveId;
        entry.actorContributorId = actorContributorId;
        entry.entityType = entityType;
        entry.entityId = entityId;
        entry.action = action;
        entry.beforeState = beforeState;
        entry.afterState = afterState;
        entry.reason = reason;
        entry.occurredAt = Instant.now();
        return entry;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public UUID getActorContributorId() {
        return actorContributorId;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public Action getAction() {
        return action;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public String getReason() {
        return reason;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getRequestId() {
        return requestId;
    }
}
