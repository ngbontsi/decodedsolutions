package com.platform.familytree.community;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(name = "invited_email", nullable = false)
    private String invitedEmail;

    @Column(name = "invited_by", nullable = false)
    private UUID invitedBy;

    @Column(nullable = false)
    private String role;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Invitation() {
    }

    public static Invitation create(UUID archiveId, String email, UUID invitedBy,
                                    String role, String tokenHash, Instant expiresAt) {
        Invitation invitation = new Invitation();
        invitation.id = Uuid7.generate();
        invitation.archiveId = archiveId;
        invitation.invitedEmail = email;
        invitation.invitedBy = invitedBy;
        invitation.role = role;
        invitation.tokenHash = tokenHash;
        invitation.expiresAt = expiresAt;
        invitation.createdAt = Instant.now();
        return invitation;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public UUID getInvitedBy() {
        return invitedBy;
    }

    public String getRole() {
        return role;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
