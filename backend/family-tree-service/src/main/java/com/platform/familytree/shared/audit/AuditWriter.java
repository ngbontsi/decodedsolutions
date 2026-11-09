package com.platform.familytree.shared.audit;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Writes audit rows inside the caller's transaction. If the audit insert fails,
 * the business change rolls back with it — a log with gaps is worse than no log.
 */
@Component
public class AuditWriter {

    private final AuditLogRepository repository;

    public AuditWriter(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(UUID archiveId,
                       UUID actorContributorId,
                       String entityType,
                       UUID entityId,
                       AuditLog.Action action,
                       String beforeState,
                       String afterState,
                       String reason) {
        repository.save(AuditLog.of(archiveId, actorContributorId, entityType, entityId,
                action, beforeState, afterState, reason));
    }
}
