package com.platform.familytree.community;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consent")
public class Consent {

    @Id
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "consent_type", nullable = false)
    private String consentType;

    @Column(name = "granted_by", nullable = false)
    private UUID grantedBy;

    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "document_id")
    private UUID documentId;

    protected Consent() {
    }

    public static Consent grant(UUID personId, String consentType, UUID grantedBy,
                                Instant expiresAt, UUID documentId) {
        Consent consent = new Consent();
        consent.id = Uuid7.generate();
        consent.personId = personId;
        consent.consentType = consentType;
        consent.grantedBy = grantedBy;
        consent.grantedAt = Instant.now();
        consent.expiresAt = expiresAt;
        consent.documentId = documentId;
        return consent;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonId() {
        return personId;
    }

    public String getConsentType() {
        return consentType;
    }

    public UUID getGrantedBy() {
        return grantedBy;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UUID getDocumentId() {
        return documentId;
    }
}
