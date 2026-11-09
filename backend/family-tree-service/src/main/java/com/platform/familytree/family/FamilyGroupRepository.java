package com.platform.familytree.family;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, UUID> {

    List<FamilyGroup> findByArchiveIdOrderByCreatedAtAsc(UUID archiveId);
}
