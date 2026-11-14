package com.platform.familytree.identity.repository;

import com.platform.familytree.identity.model.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArchiveRepository extends JpaRepository<Archive, UUID> {

    Optional<Archive> findFirstByStatusOrderByCreatedAtAsc(String status);
}
