package com.platform.familytree.family.dto;

import com.platform.familytree.family.model.CareRelationship;

import java.time.LocalDate;
import java.util.UUID;

public record RecordCareRequest(UUID caregiverId,
                                UUID recipientId,
                                CareRelationship.Type careType,
                                LocalDate startDate,
                                UUID actorId) {
}