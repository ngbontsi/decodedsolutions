package com.platform.familytree.family;

import com.platform.familytree.shared.error.DomainException;
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
 * A recognised parent-child relationship.
 *
 * <p>Four identity states, because "we do not know who" and "the family knows but
 * has not written it down" are different research problems (decision D5).
 */
@Entity
@Table(name = "parentage")
public class Parentage {

    public enum Type {
        BIOLOGICAL, ADOPTIVE, LEGAL, OTHER_RECOGNISED
    }

    public enum IdentityStatus {
        IDENTIFIED, UNIDENTIFIED, UNRECORDED, UNKNOWN
    }

    public enum Role {
        FATHER, MOTHER, PARENT, UNKNOWN
    }

    public enum Status {
        ACTIVE, SUPERSEDED, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "child_id", nullable = false)
    private UUID childId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parentage_type", nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_status", nullable = false)
    private IdentityStatus identityStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_role", nullable = false)
    private Role role = Role.UNKNOWN;

    @Column(name = "reported_name")
    private String reportedName;

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

    @Column(name = "superseded_by_id")
    private UUID supersededById;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Parentage() {
    }

    public static Parentage identified(UUID childId, UUID parentId, Type type, Role role,
                                       UUID assertedBy, LocalDate validFrom) {
        if (parentId == null) {
            throw DomainException.invalid("IDENTIFIED parentage requires a parentId");
        }
        if (parentId.equals(childId)) {
            throw DomainException.rule("A person cannot be their own parent");
        }
        Parentage parentage = base(childId, type, role, assertedBy, validFrom);
        parentage.parentId = parentId;
        parentage.identityStatus = IdentityStatus.IDENTIFIED;
        return parentage;
    }

    public static Parentage unidentified(UUID childId, Type type, Role role,
                                         UUID assertedBy, LocalDate validFrom) {
        Parentage parentage = base(childId, type, role, assertedBy, validFrom);
        parentage.identityStatus = IdentityStatus.UNIDENTIFIED;
        return parentage;
    }

    public static Parentage unrecorded(UUID childId, Type type, Role role, String reportedName,
                                       UUID assertedBy, LocalDate validFrom) {
        if (reportedName == null || reportedName.isBlank()) {
            throw DomainException.invalid("UNRECORDED parentage requires a reportedName");
        }
        Parentage parentage = base(childId, type, role, assertedBy, validFrom);
        parentage.identityStatus = IdentityStatus.UNRECORDED;
        parentage.reportedName = reportedName;
        return parentage;
    }

    public static Parentage unknown(UUID childId, Type type, Role role,
                                    UUID assertedBy, LocalDate validFrom) {
        Parentage parentage = base(childId, type, role, assertedBy, validFrom);
        parentage.identityStatus = IdentityStatus.UNKNOWN;
        return parentage;
    }

    private static Parentage base(UUID childId, Type type, Role role,
                                  UUID assertedBy, LocalDate validFrom) {
        if (childId == null) {
            throw DomainException.invalid("childId is required");
        }
        if (assertedBy == null) {
            throw DomainException.invalid("assertedBy is required");
        }
        Parentage parentage = new Parentage();
        parentage.id = Uuid7.generate();
        parentage.childId = childId;
        parentage.type = type;
        parentage.role = role == null ? Role.UNKNOWN : role;
        parentage.assertedBy = assertedBy;
        parentage.validFrom = validFrom;
        parentage.createdAt = Instant.now();
        return parentage;
    }

    /** FT-008: close the old row, never delete it. */
    public void supersedeBy(Parentage replacement, LocalDate on) {
        if (replacement == null || replacement.getId() == null) {
            throw DomainException.invalid("A superseding parentage must be persisted first");
        }
        this.status = Status.SUPERSEDED;
        this.supersededById = replacement.getId();
        this.validTo = on;
    }

    public void markEnteredInError() {
        this.status = Status.ENTERED_IN_ERROR;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChildId() {
        return childId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public Type getType() {
        return type;
    }

    public IdentityStatus getIdentityStatus() {
        return identityStatus;
    }

    public Role getRole() {
        return role;
    }

    public String getReportedName() {
        return reportedName;
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

    public UUID getSupersededById() {
        return supersededById;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
