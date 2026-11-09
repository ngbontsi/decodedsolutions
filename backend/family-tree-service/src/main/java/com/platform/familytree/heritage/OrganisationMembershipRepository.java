package com.platform.familytree.heritage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganisationMembershipRepository extends JpaRepository<OrganisationMembership, UUID> {

    List<OrganisationMembership> findByPersonIdOrderByStartDateAsc(UUID personId);

    List<OrganisationMembership> findByOrganisationIdOrderByStartDateAsc(UUID organisationId);
}
