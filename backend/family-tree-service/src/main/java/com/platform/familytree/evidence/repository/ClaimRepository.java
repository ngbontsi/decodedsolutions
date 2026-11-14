package com.platform.familytree.evidence.repository;

import com.platform.familytree.evidence.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    List<Claim> findBySubjectPersonIdOrderBySubmittedAtDesc(UUID subjectPersonId);

    List<Claim> findByArchiveIdOrderBySubmittedAtDesc(UUID archiveId);
}
