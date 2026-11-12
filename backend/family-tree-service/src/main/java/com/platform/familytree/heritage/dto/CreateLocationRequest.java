package com.platform.familytree.heritage.dto;

import com.platform.familytree.heritage.model.Location;

import java.util.UUID;

public record CreateLocationRequest(String name, Location.Type locationType,
                                    UUID parentLocationId) {
}