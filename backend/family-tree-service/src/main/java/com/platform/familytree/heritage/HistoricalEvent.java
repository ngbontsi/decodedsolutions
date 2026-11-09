package com.platform.familytree.heritage;

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
@Table(name = "historical_event")
public class HistoricalEvent {

    public enum Type {
        POLITICAL, SOCIAL, CULTURAL, LEGAL, PERSONAL, OTHER
    }

    public enum Precision {
        DAY, MONTH, YEAR, DECADE, APPROXIMATE, UNKNOWN
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_precision", nullable = false)
    private Precision datePrecision = Precision.UNKNOWN;

    @Column(name = "location_id")
    private UUID locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private Type type;

    @Column(name = "asserted_by", nullable = false)
    private UUID assertedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected HistoricalEvent() {
    }

    public static HistoricalEvent create(UUID archiveId, String title, String description,
                                         LocalDate eventDate, Precision precision,
                                         UUID locationId, Type type, UUID assertedBy) {
        HistoricalEvent event = new HistoricalEvent();
        event.id = Uuid7.generate();
        event.archiveId = archiveId;
        event.title = title;
        event.description = description;
        event.eventDate = eventDate;
        event.datePrecision = precision == null ? Precision.UNKNOWN : precision;
        event.locationId = locationId;
        event.type = type;
        event.assertedBy = assertedBy;
        event.createdAt = Instant.now();
        return event;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public Precision getDatePrecision() {
        return datePrecision;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public Type getType() {
        return type;
    }

    public UUID getAssertedBy() {
        return assertedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
