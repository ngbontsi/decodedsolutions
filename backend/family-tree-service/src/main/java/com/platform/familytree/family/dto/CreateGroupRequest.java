package com.platform.familytree.family.dto;

import com.platform.familytree.family.model.FamilyGroup;

import java.util.UUID;

public record CreateGroupRequest(String name,
                                 String description,
                                 FamilyGroup.LineageType lineageType,
                                 UUID actorId) {
}