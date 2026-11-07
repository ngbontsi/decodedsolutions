package com.platform.familytree.family;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CareRelationshipRepository extends JpaRepository<CareRelationship, UUID> {

    List<CareRelationship> findByRecipientIdOrderByCreatedAtAsc(UUID recipientId);

    List<CareRelationship> findByCaregiverIdOrderByCreatedAtAsc(UUID caregiverId);
}
