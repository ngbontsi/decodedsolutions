package com.platform.familytree.family.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_union_participant")
public class UnionParticipant {

    public enum Role {
        SPOUSE, PARTNER
    }

    @EmbeddedId
    private UnionParticipantId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_role", nullable = false)
    private Role role;

    protected UnionParticipant() {
    }

    public static UnionParticipant of(java.util.UUID unionId, java.util.UUID personId, Role role) {
        UnionParticipant participant = new UnionParticipant();
        participant.id = new UnionParticipantId(unionId, personId);
        participant.role = role == null ? Role.PARTNER : role;
        return participant;
    }

    public UnionParticipantId getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }
}
