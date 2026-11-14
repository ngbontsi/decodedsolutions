package com.platform.familytree.family.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SupersedeRequest(UUID newParentId, LocalDate on, UUID actorId) {
}