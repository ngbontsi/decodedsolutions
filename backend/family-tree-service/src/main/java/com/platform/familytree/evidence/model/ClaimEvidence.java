package com.platform.familytree.evidence.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "claim_evidence")
public class ClaimEvidence {

    public enum Stance {
        SUPPORTS, CONTRADICTS, CONTEXT
    }

    public enum Weight {
        WEAK, MODERATE, STRONG
    }

    @EmbeddedId
    private ClaimEvidenceId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stance stance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Weight weight;

    private String note;

    @Column(name = "added_by", nullable = false)
    private UUID addedBy;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    protected ClaimEvidence() {
    }

    public static ClaimEvidence attach(UUID claimId, UUID evidenceId, Stance stance,
                                       Weight weight, String note, UUID addedBy) {
        ClaimEvidence link = new ClaimEvidence();
        link.id = new ClaimEvidenceId(claimId, evidenceId);
        link.stance = stance;
        link.weight = weight == null ? Weight.MODERATE : weight;
        link.note = note;
        link.addedBy = addedBy;
        link.addedAt = Instant.now();
        return link;
    }

    public ClaimEvidenceId getId() {
        return id;
    }

    public Stance getStance() {
        return stance;
    }

    public Weight getWeight() {
        return weight;
    }

    public String getNote() {
        return note;
    }

    public UUID getAddedBy() {
        return addedBy;
    }

    public Instant getAddedAt() {
        return addedAt;
    }
}
