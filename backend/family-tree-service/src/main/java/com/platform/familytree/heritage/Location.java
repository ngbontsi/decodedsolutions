package com.platform.familytree.heritage;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "location")
public class Location {

    public enum Type {
        COUNTRY, PROVINCE, TOWN, SITE, ADDRESS, OTHER
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private Type type;

    @Column(name = "parent_location_id")
    private UUID parentLocationId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String description;

    protected Location() {
    }

    public static Location create(UUID archiveId, String name, Type type, UUID parentLocationId) {
        Location location = new Location();
        location.id = Uuid7.generate();
        location.archiveId = archiveId;
        location.name = name;
        location.type = type;
        location.parentLocationId = parentLocationId;
        return location;
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

    public UUID getParentLocationId() {
        return parentLocationId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }
}
