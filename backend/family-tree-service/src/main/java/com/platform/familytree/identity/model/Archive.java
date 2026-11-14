package com.platform.familytree.identity.model;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "archive")
public class Archive {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Archive() {
    }

    public static Archive create(String name, String description) {
        Archive archive = new Archive();
        archive.id = Uuid7.generate();
        archive.name = name;
        archive.description = description;
        archive.createdAt = Instant.now();
        return archive;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
