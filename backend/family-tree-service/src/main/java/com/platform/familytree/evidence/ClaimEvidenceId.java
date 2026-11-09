package com.platform.familytree.evidence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ClaimEvidenceId implements Serializable {

    @Column(name = "claim_id")
    private UUID claimId;

    @Column(name = "evidence_id")
    private UUID evidenceId;

    protected ClaimEvidenceId() {
    }

    public ClaimEvidenceId(UUID claimId, UUID evidenceId) {
        this.claimId = claimId;
        this.evidenceId = evidenceId;
    }

    public UUID getClaimId() {
        return claimId;
    }

    public UUID getEvidenceId() {
        return evidenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClaimEvidenceId other)) {
            return false;
        }
        return Objects.equals(claimId, other.claimId) && Objects.equals(evidenceId, other.evidenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claimId, evidenceId);
    }
}
