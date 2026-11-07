package com.platform.familytree.community;

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
@Table(name = "contributor")
public class Contributor {

    public enum Status {
        ACTIVE, SUSPENDED, DEACTIVATED
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected Contributor() {
    }

    public static Contributor create(UUID archiveId, String displayName, String email) {
        Contributor contributor = new Contributor();
        contributor.id = Uuid7.generate();
        contributor.archiveId = archiveId;
        contributor.displayName = displayName;
        contributor.email = email;
        contributor.joinedAt = Instant.now();
        return contributor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
