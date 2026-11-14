package com.platform.familytree.community.model;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "permission")
public class Permission {

    public enum ScopeType {
        ARCHIVE, FAMILY, PERSON
    }

    public enum Role {
        VIEWER, CONTRIBUTOR, REVIEWER, ADMIN
    }

    @Id
    private UUID id;

    @Column(name = "contributor_id", nullable = false)
    private UUID contributorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false)
    private ScopeType scopeType;

    @Column(name = "scope_id", nullable = false)
    private UUID scopeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected Permission() {
    }

    public static Permission grant(UUID contributorId, ScopeType scopeType, UUID scopeId,
                                   Role role) {
        Permission permission = new Permission();
        permission.id = Uuid7.generate();
        permission.contributorId = contributorId;
        permission.scopeType = scopeType;
        permission.scopeId = scopeId;
        permission.role = role;
        return permission;
    }

    public UUID getId() {
        return id;
    }

    public UUID getContributorId() {
        return contributorId;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public UUID getScopeId() {
        return scopeId;
    }

    public Role getRole() {
        return role;
    }
}
