package com.platform.familytree.identity.dto;

import com.platform.familytree.identity.model.Person;

import java.time.LocalDate;
import java.util.UUID;

public record DeathRequest(LocalDate deathDate,
                           Person.DatePrecision precision,
                           UUID actorId) {
}
