package com.platform.familytree.family;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "family_group")
public class FamilyGroup {

    public enum LineageType {
        PATRILINEAL, MATRILINEAL, BILATERAL, UNKNOWN
    }

    public enum Status {
        ACTIVE, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "lineage_type", nullable = false)
    private LineageType lineageType = LineageType.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected FamilyGroup() {
    }

    public static FamilyGroup create(UUID archiveId, String name, String description,
                                     LineageType lineageType) {
        FamilyGroup group = new FamilyGroup();
        group.id = Uuid7.generate();
        group.archiveId = archiveId;
        group.name = name;
        group.description = description;
        group.lineageType = lineageType == null ? LineageType.UNKNOWN : lineageType;
        group.createdAt = Instant.now();
        return group;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LineageType getLineageType() {
        return lineageType;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
