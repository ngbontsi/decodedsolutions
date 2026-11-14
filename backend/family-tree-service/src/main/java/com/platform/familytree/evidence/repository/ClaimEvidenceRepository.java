package com.platform.familytree.evidence.repository;

import com.platform.familytree.evidence.model.ClaimEvidence;
import com.platform.familytree.evidence.model.ClaimEvidenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimEvidenceRepository extends JpaRepository<ClaimEvidence, ClaimEvidenceId> {

    List<ClaimEvidence> findByIdClaimId(UUID claimId);
}
