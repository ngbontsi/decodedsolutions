package com.platform.familytree.family.repository;

import com.platform.familytree.family.model.FamilyUnion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyUnionRepository extends JpaRepository<FamilyUnion, UUID> {

    List<FamilyUnion> findByArchiveIdOrderByCreatedAtAsc(UUID archiveId);
}
