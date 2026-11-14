package com.platform.familytree.evidence.dto;

import java.util.UUID;

public record CreateClaimRequest(UUID subjectPersonId,
                                 UUID subjectOrganisationId,
                                 UUID subjectEventId,
                                 String statement,
                                 UUID actorId) {
}