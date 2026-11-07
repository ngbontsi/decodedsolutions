package com.platform.familytree.family;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * An assertion that a person belongs to a family group.
 *
 * <p>Deliberately NOT derived from parentage. Decision D3: biology and belonging
 * are different claims, recorded by different people for different reasons.
 */
@Entity
@Table(name = "family_membership")
public class FamilyMembership {

    public enum ConnectionType {
        MEMBER, HEAD, ELDER, DESCENDANT, AFFILIATE
    }

    public enum Basis {
        PARENTAGE, BIRTH, MARRIAGE, ADOPTION, CULTURAL, SOCIAL, LEGAL, SELF_IDENTIFIED, OTHER
    }

    public enum Status {
        ACTIVE, WITHDRAWN, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "family_group_id", nullable = false)
    private UUID familyGroupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false)
    private ConnectionType connectionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Basis basis;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "asserted_by", nullable = false)
    private UUID assertedBy;

    @Column(name = "source_id")
    private UUID sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected FamilyMembership() {
    }

    public static FamilyMembership of(UUID personId,
                                          UUID familyGroupId,
                                          ConnectionType connectionType,
                                          Basis basis,
                                          UUID assertedBy,
                                          LocalDate validFrom) {
        FamilyMembership membership = new FamilyMembership();
        membership.id = Uuid7.generate();
        membership.personId = personId;
        membership.familyGroupId = familyGroupId;
        membership.connectionType = connectionType;
        membership.basis = basis;
        membership.assertedBy = assertedBy;
        membership.validFrom = validFrom;
        membership.createdAt = Instant.now();
        return membership;
    }

    public void withdraw(LocalDate on) {
        this.status = Status.WITHDRAWN;
        this.validTo = on;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonId() {
        return personId;
    }

    public UUID getFamilyGroupId() {
        return familyGroupId;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public Basis getBasis() {
        return basis;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public UUID getAssertedBy() {
        return assertedBy;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
