package com.platform.familytree.heritage.dto;

import com.platform.familytree.heritage.model.Organisation;

import java.time.LocalDate;
import java.util.UUID;

public record CreateOrganisationRequest(String name,
                                        Organisation.Type organisationType,
                                        LocalDate foundedDate,
                                        String description,
                                        UUID actorId) {
}