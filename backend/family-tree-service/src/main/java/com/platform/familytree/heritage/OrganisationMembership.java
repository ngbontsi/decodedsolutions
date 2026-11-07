package com.platform.familytree.heritage;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "organisation_membership")
public class OrganisationMembership {

    @Id
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Column(name = "organisation_id", nullable = false)
    private UUID organisationId;

    private String role;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "asserted_by", nullable = false)
    private UUID assertedBy;

    protected OrganisationMembership() {
    }

    public static OrganisationMembership of(UUID personId, UUID organisationId, String role,
                                            LocalDate startDate, UUID assertedBy) {
        OrganisationMembership membership = new OrganisationMembership();
        membership.id = Uuid7.generate();
        membership.personId = personId;
        membership.organisationId = organisationId;
        membership.role = role;
        membership.startDate = startDate;
        membership.assertedBy = assertedBy;
        return membership;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonId() {
        return personId;
    }

    public UUID getOrganisationId() {
        return organisationId;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public UUID getAssertedBy() {
        return assertedBy;
    }
}
