package com.platform.familytree.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimEvidenceRepository extends JpaRepository<ClaimEvidence, ClaimEvidenceId> {

    List<ClaimEvidence> findByIdClaimId(UUID claimId);
}
