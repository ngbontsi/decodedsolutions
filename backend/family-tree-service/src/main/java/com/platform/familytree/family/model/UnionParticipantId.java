package com.platform.familytree.family.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UnionParticipantId implements Serializable {

    @Column(name = "union_id")
    private UUID unionId;

    @Column(name = "person_id")
    private UUID personId;

    protected UnionParticipantId() {
    }

    public UnionParticipantId(UUID unionId, UUID personId) {
        this.unionId = unionId;
        this.personId = personId;
    }

    public UUID getUnionId() {
        return unionId;
    }

    public UUID getPersonId() {
        return personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnionParticipantId other)) {
            return false;
        }
        return Objects.equals(unionId, other.unionId) && Objects.equals(personId, other.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unionId, personId);
    }
}
