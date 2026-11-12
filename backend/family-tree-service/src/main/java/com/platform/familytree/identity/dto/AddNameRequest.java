package com.platform.familytree.identity.dto;

import com.platform.familytree.identity.model.PersonName;

import java.time.LocalDate;
import java.util.UUID;

public record AddNameRequest(PersonName.Type nameType,
                             String fullName,
                             String givenNames,
                             String familyName,
                             LocalDate validFrom,
                             Boolean preferred,
                             UUID actorId) {
}
