package com.platform.familytree.family.model;

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

/** "union" is reserved in SQL, so the table is family_union. */
@Entity
@Table(name = "family_union")
public class FamilyUnion {

    public enum Type {
        MARRIAGE, CIVIL_UNION, CUSTOMARY, COHABITATION, PARTNERSHIP, UNKNOWN
    }

    public enum Status {
        ACTIVE, ENDED, ANNULLED, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Enumerated(EnumType.STRING)
    @Column(name = "union_type", nullable = false)
    private Type type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "asserted_by", nullable = false)
    private UUID assertedBy;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected FamilyUnion() {
    }

    public static FamilyUnion create(UUID archiveId, Type type, LocalDate startDate,
                                     UUID assertedBy) {
        FamilyUnion union = new FamilyUnion();
        union.id = Uuid7.generate();
        union.archiveId = archiveId;
        union.type = type == null ? Type.UNKNOWN : type;
        union.startDate = startDate;
        union.assertedBy = assertedBy;
        union.createdAt = Instant.now();
        return union;
    }

    public void end(LocalDate on) {
        if (startDate != null && on != null && on.isBefore(startDate)) {
            throw com.platform.familytree.shared.error.DomainException.rule(
                    "A union cannot end before it starts");
        }
        this.endDate = on;
        this.status = Status.ENDED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
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

    public Status getStatus() {
        return status;
    }

    public UUID getAssertedBy() {
        return assertedBy;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
