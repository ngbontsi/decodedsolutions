package com.platform.familytree.family.repository;

import com.platform.familytree.family.model.CareRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CareRelationshipRepository extends JpaRepository<CareRelationship, UUID> {

    List<CareRelationship> findByRecipientIdOrderByCreatedAtAsc(UUID recipientId);

    List<CareRelationship> findByCaregiverIdOrderByCreatedAtAsc(UUID caregiverId);
}
