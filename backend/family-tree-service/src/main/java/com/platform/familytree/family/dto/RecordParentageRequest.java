package com.platform.familytree.family.dto;

import com.platform.familytree.family.model.Parentage;

import java.time.LocalDate;
import java.util.UUID;

public record RecordParentageRequest(UUID childId,
                                     UUID parentId,
                                     Parentage.Type parentageType,
                                     Parentage.Role parentRole,
                                     Parentage.IdentityStatus identityStatus,
                                     String reportedName,
                                     LocalDate validFrom,
                                     UUID actorId) {
}