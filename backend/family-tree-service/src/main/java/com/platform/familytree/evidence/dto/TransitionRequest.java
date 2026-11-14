package com.platform.familytree.evidence.dto;

import com.platform.familytree.evidence.model.Claim;

import java.util.UUID;

public record TransitionRequest(Claim.Status status, UUID actorId) {
}