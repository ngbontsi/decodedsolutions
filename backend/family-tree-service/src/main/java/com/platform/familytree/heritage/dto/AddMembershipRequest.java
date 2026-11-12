package com.platform.familytree.heritage.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AddMembershipRequest(UUID personId,
                                   String role,
                                   LocalDate startDate,
                                   UUID actorId) {
}
