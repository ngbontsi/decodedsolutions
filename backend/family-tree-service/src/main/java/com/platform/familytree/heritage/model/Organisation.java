package com.platform.familytree.heritage.model;

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

@Entity
@Table(name = "organisation")
public class Organisation {

    public enum Type {
        POLITICAL, CIVIC, RELIGIOUS, EDUCATION, MILITARY, BUSINESS, OTHER
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "organisation_type", nullable = false)
    private Type type;

    @Column(name = "founded_date")
    private LocalDate foundedDate;

    @Column(name = "dissolved_date")
    private LocalDate dissolvedDate;

    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Organisation() {
    }

    public static Organisation create(UUID archiveId, String name, Type type,
                                      LocalDate foundedDate, String description) {
        Organisation organisation = new Organisation();
        organisation.id = Uuid7.generate();
        organisation.archiveId = archiveId;
        organisation.name = name;
        organisation.type = type;
        organisation.foundedDate = foundedDate;
        organisation.description = description;
        organisation.createdAt = Instant.now();
        return organisation;
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

    public Type getType() {
        return type;
    }

    public LocalDate getFoundedDate() {
        return foundedDate;
    }

    public LocalDate getDissolvedDate() {
        return dissolvedDate;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
