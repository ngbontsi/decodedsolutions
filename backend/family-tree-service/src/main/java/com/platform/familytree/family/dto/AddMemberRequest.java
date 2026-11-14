package com.platform.familytree.family.dto;

import com.platform.familytree.family.model.FamilyMembership;

import java.time.LocalDate;
import java.util.UUID;

public record AddMemberRequest(UUID personId,
                               FamilyMembership.ConnectionType connectionType,
                               FamilyMembership.Basis basis,
                               LocalDate validFrom,
                               UUID actorId) {
}