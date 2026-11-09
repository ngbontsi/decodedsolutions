package com.platform.familytree.heritage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {

    List<Organisation> findByArchiveIdOrderByNameAsc(UUID archiveId);
}
