package com.platform.familytree.family.repository;

import com.platform.familytree.family.model.FamilyMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyMembershipRepository extends JpaRepository<FamilyMembership, UUID> {

    List<FamilyMembership> findByPersonIdOrderByCreatedAtAsc(UUID personId);

    List<FamilyMembership> findByFamilyGroupIdOrderByCreatedAtAsc(UUID familyGroupId);

    long countByPersonIdAndFamilyGroupIdAndStatus(UUID personId, UUID familyGroupId,
                                                  FamilyMembership.Status status);
}
