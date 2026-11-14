package com.platform.familytree.evidence.dto;


import com.platform.familytree.evidence.model.ClaimEvidence;

import java.util.UUID;

public record AttachEvidenceRequest(UUID documentId,
                                    ClaimEvidence.Stance stance,
                                    ClaimEvidence.Weight weight,
                                    String note,
                                    UUID actorId) {
}
