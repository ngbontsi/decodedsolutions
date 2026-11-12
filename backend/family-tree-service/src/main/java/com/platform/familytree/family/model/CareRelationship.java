package com.platform.familytree.family.model;

import com.platform.familytree.shared.error.DomainException;
import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Someone raised or cared for someone. Never implies parentage (FT-002). */
@Entity
@Table(name = "care_relationship")
public class CareRelationship {

    public enum Type {
        STEP_PARENT, GUARDIAN, FOSTER_PARENT, CAREGIVER, OTHER
    }

    public enum Status {
        ACTIVE, ENDED, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "caregiver_id", nullable = false)
    private UUID caregiverId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "care_type", nullable = false)
    private Type type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "asserted_by", nullable = false)
    private UUID assertedBy;

    @Column(name = "source_id")
    private UUID sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CareRelationship() {
    }

    public static CareRelationship record(UUID caregiverId, UUID recipientId, Type type,
                                          LocalDate startDate, UUID assertedBy) {
        if (caregiverId.equals(recipientId)) {
            throw DomainException.rule("A person cannot be their own caregiver");
        }
        CareRelationship care = new CareRelationship();
        care.id = Uuid7.generate();
        care.caregiverId = caregiverId;
        care.recipientId = recipientId;
        care.type = type;
        care.startDate = startDate;
        care.assertedBy = assertedBy;
        care.createdAt = Instant.now();
        return care;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCaregiverId() {
        return caregiverId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public UUID getAssertedBy() {
        return assertedBy;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
