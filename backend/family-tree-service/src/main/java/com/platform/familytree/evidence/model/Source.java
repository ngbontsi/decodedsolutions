package com.platform.familytree.evidence.model;

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
@Table(name = "source")
public class Source {

    public enum Type {
        FAMILY_TESTIMONY, DOCUMENT, PHOTOGRAPH, AUDIO, VIDEO,
        PUBLICATION, ARCHIVE_RECORD, GOVERNMENT_RECORD, ORAL_HISTORY, OTHER
    }

    public enum AccessLevel {
        PUBLIC, FAMILY, RESTRICTED, PRIVATE
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private Type type;

    @Column(nullable = false)
    private String title;

    @Column(name = "author_or_speaker")
    private String authorOrSpeaker;

    private String institution;

    @Column(name = "reference_code")
    private String referenceCode;

    @Column(name = "recorded_date")
    private LocalDate recordedDate;

    @Column(name = "location_id")
    private UUID locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private AccessLevel accessLevel = AccessLevel.FAMILY;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Source() {
    }

    public static Source create(UUID archiveId, Type type, String title, String authorOrSpeaker) {
        Source source = new Source();
        source.id = Uuid7.generate();
        source.archiveId = archiveId;
        source.type = type;
        source.title = title;
        source.authorOrSpeaker = authorOrSpeaker;
        source.createdAt = Instant.now();
        return source;
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

    public String getTitle() {
        return title;
    }

    public String getAuthorOrSpeaker() {
        return authorOrSpeaker;
    }

    public String getInstitution() {
        return institution;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public LocalDate getRecordedDate() {
        return recordedDate;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
